package edu.utexas.mediator.core;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import edu.utexas.mediator.Enumerations.TranType;
import edu.utexas.mediator.ast.AstNode;
import edu.utexas.mediator.ast.AstTerm;
import edu.utexas.mediator.ast.AttrDecl;
import edu.utexas.mediator.ast.DeleteNode;
import edu.utexas.mediator.ast.InsertNode;
import edu.utexas.mediator.ast.Program;
import edu.utexas.mediator.ast.RelDecl;
import edu.utexas.mediator.ast.RelationNode;
import edu.utexas.mediator.ast.Schema;
import edu.utexas.mediator.ast.Transaction;
import edu.utexas.mediator.ast.TupleNode;
import edu.utexas.mediator.ast.UpdateNode;
import edu.utexas.mediator.constraint.Context;
import edu.utexas.mediator.constraint.Invariant;
import edu.utexas.mediator.constraint.LEqTerm;
import edu.utexas.mediator.constraint.LEquiJoin;
import edu.utexas.mediator.constraint.LProject;

public class PrunedSynthesizer implements ISynthesizer {

  private static final String UUID = "UUID_";
  private Context ctx;

  public PrunedSynthesizer() {
    ctx = new Context();
  }

  @Override
  public Invariant synthesize(Program src, Program tgt) {
    Invariant inv = ctx.mkInvariant();
    List<Transaction> trans = findAllInsertionTrans(src);
    for (Transaction srcTran : trans) {
      String tranName = srcTran.getSignature().getName();
      assert tgt.containsTransaction(tranName) : "Target program does not have: " + tranName;
      Transaction tgtTran = tgt.getTransactionByName(tranName);
      ArgMapping tgtMapping = findArgMapping(tgtTran, tgt.getSchema());
      // lhs has no join
      for (AstNode stmt : srcTran.getStatements()) {
        List<LEqTerm> eqs = genInvOneLhs((InsertNode) stmt, tgtMapping);
        eqs.forEach((eq) -> inv.addConstraint(eq));
      }
      // lhs has a join
      List<AstNode> stmts = srcTran.getStatements();
      for (int i = 0; i < stmts.size(); ++i) {
        InsertNode ins = (InsertNode) stmts.get(i);
        for (int j = 0; j < stmts.size(); ++j) {
          if (j == i) continue; // skip self-join
          InsertNode ref = (InsertNode) stmts.get(j);
          if (isJoinable(ins.getRelation().getRelDecl(), ref.getRelation().getRelDecl())) {
            List<LEqTerm> eqs = genInvTwoLhs(ins, ref, tgtMapping);
            eqs.forEach((eq) -> inv.addConstraint(eq));
          }
        }
      }
    }
    return inv;
  }

  /**
   * Generate all equal terms that has only one relation on LHS
   * 
   * @param ins
   *          the insertion statement
   * @param tgtMapping
   *          argument mapping in the target schema
   * @return all equal terms
   */
  protected List<LEqTerm> genInvOneLhs(InsertNode ins, ArgMapping tgtMapping) {
    List<LEqTerm> ret = new ArrayList<>();
    RelDecl relDecl = ins.getRelation().getRelDecl();
    List<AttrDecl> lhsAttrs = new ArrayList<>();
    List<String> args = new ArrayList<>();
    List<String> values = ins.getTuple().getValues();
    // generate lhsAttrs and args by discarding UUID's
    for (int i = 0; i < values.size(); ++i) {
      if (!values.get(i).startsWith(UUID)) {
        lhsAttrs.add(relDecl.getAttrDecls().get(i));
        args.add(values.get(i));
      }
    }
    // generate all possible EqTerms on the rhs
    List<List<AttrDecl>> tgtAttrList = genAttrDeclList(args, tgtMapping);
    for (List<AttrDecl> attrs : tgtAttrList) {
      Set<RelDecl> rels = findAttrRelations(attrs);
      assert rels.size() > 0;
      if (rels.size() == 1) {
        // rhs just has one relation
        RelDecl rhsRel = rels.iterator().next();
        ret.add(genEqTerm(lhsAttrs, relDecl, attrs, rhsRel));
      } else {
        // rhs has two relations or more
        ret.addAll(genInvOneLhsMultiRhs(lhsAttrs, relDecl, attrs, rels));
      }
    }
    return ret;
  }

  private List<LEqTerm> genInvOneLhsMultiRhs(List<AttrDecl> lhsAttrs, RelDecl lhs,
      List<AttrDecl> rhsAttrs, Set<RelDecl> rhs) {
    int num = rhs.size();
    assert num > 1;
    Set<LEqTerm> ret = new LinkedHashSet<>();
    List<RelDecl> rhsList = new ArrayList<>(rhs);
    for (int i = 0; i < num; ++i) {
      RelDecl rel = rhsList.get(i);
      for (int j = 0; j < num; ++j) {
        if (j == i) continue; // skip self-join
        RelDecl ref = rhsList.get(j);
        if (!isJoinable(rel, ref)) continue; // skip relations that cannot join
        {
          // generate Pi(R) = Pi(R1 \Join R2)
          List<Integer> indices = getOccurrenceIndices(rhsAttrs, rel, ref);
          List<AttrDecl> lhsSubAttrs = getSubsequence(lhsAttrs, indices);
          List<AttrDecl> rhsSubAttrs = getSubsequence(rhsAttrs, indices);
          ret.add(genEqTerm(lhsSubAttrs, lhs, rhsSubAttrs, rel, ref));
        }
        {
          // generate Pi(R) = Pi(R1)
          List<Integer> indices = getOccurrenceIndices(rhsAttrs, rel);
          if (!indices.isEmpty()) {
            List<AttrDecl> lhsSubAttrs = getSubsequence(lhsAttrs, indices);
            List<AttrDecl> rhsSubAttrs = getSubsequence(rhsAttrs, indices);
            ret.add(genEqTerm(lhsSubAttrs, lhs, rhsSubAttrs, rel));
          }
        }
        {
          // generate Pi(R) = Pi(R2)
          List<Integer> indices = getOccurrenceIndices(rhsAttrs, ref);
          if (!indices.isEmpty()) {
            List<AttrDecl> lhsSubAttrs = getSubsequence(lhsAttrs, indices);
            List<AttrDecl> rhsSubAttrs = getSubsequence(rhsAttrs, indices);
            ret.add(genEqTerm(lhsSubAttrs, lhs, rhsSubAttrs, ref));
          }
        }
      }
    }
    return new LinkedList<>(ret);
  }

  /**
   * Generate all equal terms that has one join on LHS
   * 
   * @param ins
   *          the insertion statement of relation with fk
   * @param ref
   *          the insertion statement of relation with pk
   * @param tgtMapping
   *          argument mapping in the target schema
   * @return all equal terms
   */
  protected List<LEqTerm> genInvTwoLhs(InsertNode ins, InsertNode ref, ArgMapping tgtMapping) {
    List<LEqTerm> ret = new ArrayList<>();
    RelDecl relDecl = ins.getRelation().getRelDecl();
    RelDecl refDecl = ref.getRelation().getRelDecl();
    List<AttrDecl> lhsAttrs = new ArrayList<>();
    List<String> args = new ArrayList<>();
    List<String> insValues = ins.getTuple().getValues();
    // generate lhsAttrs and args by discarding UUID's
    for (int i = 0; i < insValues.size(); ++i) {
      if (!insValues.get(i).startsWith(UUID)) {
        lhsAttrs.add(relDecl.getAttrDecls().get(i));
        args.add(insValues.get(i));
      }
    }
    List<String> refValues = ref.getTuple().getValues();
    for (int i = 0; i < refValues.size(); ++i) {
      if (!refValues.get(i).startsWith(UUID)) {
        lhsAttrs.add(refDecl.getAttrDecls().get(i));
        args.add(refValues.get(i));
      }
    }
    // generate all possible EqTerms on the rhs
    List<List<AttrDecl>> tgtAttrList = genAttrDeclList(args, tgtMapping);
    for (List<AttrDecl> attrs : tgtAttrList) {
      Set<RelDecl> rels = findAttrRelations(attrs);
      assert rels.size() > 0;
      if (rels.size() == 1) {
        // rhs just has one relation
        RelDecl rhsRel = rels.iterator().next();
        ret.add(genEqTerm(lhsAttrs, relDecl, refDecl, attrs, rhsRel));
      } else {
        // rhs has two relations or more
        ret.addAll(genInvTwoLhsMultiRhs(lhsAttrs, relDecl, refDecl, attrs, rels));
      }
    }
    return ret;
  }

  private List<LEqTerm> genInvTwoLhsMultiRhs(List<AttrDecl> lhsAttrs, RelDecl lhs, RelDecl lhsRef,
      List<AttrDecl> rhsAttrs, Set<RelDecl> rhs) {
    int num = rhs.size();
    assert num > 1;
    Set<LEqTerm> ret = new LinkedHashSet<>();
    List<RelDecl> rhsList = new ArrayList<>(rhs);
    for (int i = 0; i < num; ++i) {
      RelDecl rel = rhsList.get(i);
      for (int j = 0; j < num; ++j) {
        if (j == i) continue; // skip self-join
        RelDecl ref = rhsList.get(j);
        if (!isJoinable(rel, ref)) continue; // skip relations that cannot join
        {
          // generate Pi(R1 \Join R2) = Pi(R3 \Join R4)
          List<Integer> indices = getOccurrenceIndices(rhsAttrs, rel, ref);
          List<AttrDecl> lhsSubAttrs = getSubsequence(lhsAttrs, indices);
          List<AttrDecl> rhsSubAttrs = getSubsequence(rhsAttrs, indices);
          if (findAttrRelations(lhsSubAttrs).size() == 2) {
            ret.add(genEqTerm(lhsSubAttrs, lhs, lhsRef, rhsSubAttrs, rel, ref));
          }
        }
        {
          // generate Pi(R1 \Join R2) = Pi(R3)
          List<Integer> indices = getOccurrenceIndices(rhsAttrs, rel);
          if (!indices.isEmpty()) {
            List<AttrDecl> lhsSubAttrs = getSubsequence(lhsAttrs, indices);
            List<AttrDecl> rhsSubAttrs = getSubsequence(rhsAttrs, indices);
            if (findAttrRelations(lhsSubAttrs).size() == 2) {
              ret.add(genEqTerm(lhsSubAttrs, lhs, lhsRef, rhsSubAttrs, rel));
            }
          }
        }
        {
          // generate Pi(R1 \Join R2) = Pi(R4)
          List<Integer> indices = getOccurrenceIndices(rhsAttrs, ref);
          if (!indices.isEmpty()) {
            List<AttrDecl> lhsSubAttrs = getSubsequence(lhsAttrs, indices);
            List<AttrDecl> rhsSubAttrs = getSubsequence(rhsAttrs, indices);
            if (findAttrRelations(lhsSubAttrs).size() == 2) {
              ret.add(genEqTerm(lhsSubAttrs, lhs, lhsRef, rhsSubAttrs, ref));
            }
          }
        }
      }
    }
    return new LinkedList<>(ret);
  }

  private List<Integer> getOccurrenceIndices(List<AttrDecl> attrs, RelDecl rel) {
    List<Integer> indices = new LinkedList<>();
    int len = attrs.size();
    for (int i = 0; i < len; ++i) {
      if (attrs.get(i).getRelDecl().equals(rel)) {
        indices.add(i);
      }
    }
    return indices;
  }

  private List<Integer> getOccurrenceIndices(List<AttrDecl> attrs, RelDecl rel, RelDecl ref) {
    List<Integer> indices = new LinkedList<>();
    int len = attrs.size();
    for (int i = 0; i < len; ++i) {
      if (attrs.get(i).getRelDecl().equals(rel) || attrs.get(i).getRelDecl().equals(ref)) {
        indices.add(i);
      }
    }
    return indices;
  }

  private List<AttrDecl> getSubsequence(List<AttrDecl> attrs, List<Integer> indices) {
    List<AttrDecl> ret = new ArrayList<>();
    indices.forEach((index) -> ret.add(attrs.get(index)));
    return ret;
  }

  // check if two relations can join directly
  private boolean isJoinable(RelDecl rel, RelDecl ref) {
    for (AttrDecl attr : rel.getAttrDecls()) {
      if (attr.isForeignKey()) {
        if (attr.getReferenceAttrDecl().getRelDecl().equals(ref)) return true;
      }
    }
    return false;
  }

  // generate all possible lists of attributes given arguments and the mapping
  public List<List<AttrDecl>> genAttrDeclList(List<String> args, ArgMapping mapping) {
    return genAttrDeclList(args, 0, mapping);
  }

  private List<List<AttrDecl>> genAttrDeclList(List<String> args, int cur, ArgMapping mapping) {
    List<List<AttrDecl>> res = new ArrayList<>();
    if (cur >= args.size()) return res;
    for (AttrDecl attr : mapping.getMapping(args.get(cur))) {
      List<List<AttrDecl>> list = genAttrDeclList(args, cur + 1, mapping);
      if (list.isEmpty()) {
        List<AttrDecl> innerRes = new ArrayList<>();
        innerRes.add(attr);
        res.add(innerRes);
      } else {
        for (List<AttrDecl> innerList : list) {
          List<AttrDecl> innerRes = new ArrayList<>();
          innerRes.add(attr);
          innerRes.addAll(innerList);
          res.add(innerRes);
        }
      }
    }
    return res;
  }

  // template
  protected LEqTerm genEqTerm(List<AttrDecl> srcAttrs, RelDecl srcRel, List<AttrDecl> tgtAttrs,
      RelDecl tgtRel) {
    List<String> lhsAttrs = srcAttrs.stream().map(AttrDecl::getName).collect(Collectors.toList());
    List<String> rhsAttrs = tgtAttrs.stream().map(AttrDecl::getName).collect(Collectors.toList());
    LProject lhs = ctx.mkProject(ctx.mkAttrList(lhsAttrs), ctx.mkRelConst(srcRel));
    LProject rhs = ctx.mkProject(ctx.mkAttrList(rhsAttrs), ctx.mkRelConst(tgtRel));
    return ctx.mkEqTerm(lhs, rhs);
  }

  // template
  protected LEqTerm genEqTerm(List<AttrDecl> srcAttrs, RelDecl srcRel, List<AttrDecl> tgtAttrs,
      RelDecl tgtRel, RelDecl tgtRefRel) {
    List<String> lhsAttrs = srcAttrs.stream().map(AttrDecl::getName).collect(Collectors.toList());
    List<String> rhsAttrs = tgtAttrs.stream().map(AttrDecl::getName).collect(Collectors.toList());
    LProject lhs = ctx.mkProject(ctx.mkAttrList(lhsAttrs), ctx.mkRelConst(srcRel));
    LProject rhs = ctx.mkProject(ctx.mkAttrList(rhsAttrs), genEquiJoinTerm(tgtRel, tgtRefRel));
    return ctx.mkEqTerm(lhs, rhs);
  }

  // template
  protected LEqTerm genEqTerm(List<AttrDecl> srcAttrs, RelDecl srcRel, RelDecl srcRefRel,
      List<AttrDecl> tgtAttrs, RelDecl tgtRel) {
    List<String> lhsAttrs = srcAttrs.stream().map(AttrDecl::getName).collect(Collectors.toList());
    List<String> rhsAttrs = tgtAttrs.stream().map(AttrDecl::getName).collect(Collectors.toList());
    LProject lhs = ctx.mkProject(ctx.mkAttrList(lhsAttrs), genEquiJoinTerm(srcRel, srcRefRel));
    LProject rhs = ctx.mkProject(ctx.mkAttrList(rhsAttrs), ctx.mkRelConst(tgtRel));
    return ctx.mkEqTerm(lhs, rhs);
  }

  // template
  protected LEqTerm genEqTerm(List<AttrDecl> srcAttrs, RelDecl srcRel, RelDecl srcRefRel,
      List<AttrDecl> tgtAttrs, RelDecl tgtRel, RelDecl tgtRefRel) {
    List<String> lhsAttrs = srcAttrs.stream().map(AttrDecl::getName).collect(Collectors.toList());
    List<String> rhsAttrs = tgtAttrs.stream().map(AttrDecl::getName).collect(Collectors.toList());
    LProject lhs = ctx.mkProject(ctx.mkAttrList(lhsAttrs), genEquiJoinTerm(srcRel, srcRefRel));
    LProject rhs = ctx.mkProject(ctx.mkAttrList(rhsAttrs), genEquiJoinTerm(tgtRel, tgtRefRel));
    return ctx.mkEqTerm(lhs, rhs);
  }

  private LEquiJoin genEquiJoinTerm(RelDecl rel, RelDecl ref) {
    assert isJoinable(rel, ref);
    AttrDecl fk = null;
    // find the foreign key
    for (AttrDecl attr : rel.getAttrDecls()) {
      if (attr.isForeignKey() && attr.getReferenceAttrDecl().getRelDecl().equals(ref)) {
        fk = attr;
        break;
      }
    }
    assert fk != null;
    return ctx.mkEquiJoin(ctx.mkRelConst(rel), ctx.mkRelConst(ref), ctx.mkAttrConst(fk.getName()),
        ctx.mkAttrConst(fk.getReferenceAttrDecl().getName()));
  }

  protected TranType getTranType(Transaction tran) {
    assert tran.getStatements().size() > 0;
    // infer transaction type by the first statement
    AstNode stmt = tran.getStatements().get(0);
    if (stmt instanceof AstTerm) {
      return TranType.QUERY;
    } else if (stmt instanceof InsertNode) {
      return TranType.INSERTION;
    } else if (stmt instanceof DeleteNode) {
      return TranType.DELETION;
    } else if (stmt instanceof UpdateNode) {
      return TranType.UPDATE;
    } else {
      return TranType.UNKNOWN;
    }
  }

  private ArgMapping findArgMapping(Transaction tran, Schema schema) {
    ArgMapping mapping = new ArgMapping();
    for (String arg : tran.getSignature().getArguments()) {
      for (AstNode stmt : tran.getStatements()) {
        assert stmt instanceof InsertNode;
        RelationNode rel = ((InsertNode) stmt).getRelation();
        TupleNode tuple = ((InsertNode) stmt).getTuple();
        List<String> values = tuple.getValues();
        for (int i = 0; i < values.size(); ++i) {
          if (values.get(i).equals(arg)) {
            AttrDecl attr = findAttrByIndex(schema, rel.getName(), i);
            mapping.addMapping(arg, attr);
          }
        }
      }
    }
    return mapping;
  }

  private List<Transaction> findAllInsertionTrans(Program prog) {
    List<Transaction> trans = prog.getTransactions();
    List<Transaction> ins = trans.stream().filter((tran) -> getTranType(tran) == TranType.INSERTION)
        .collect(Collectors.toList());
    return ins;
  }

  private Set<RelDecl> findAttrRelations(List<AttrDecl> attrs) {
    return attrs.stream().map(AttrDecl::getRelDecl).collect(Collectors.toSet());
  }

  private AttrDecl findAttrByIndex(Schema schema, String rel, int index) {
    RelDecl relDecl = schema.getRelDeclByName(rel);
    List<AttrDecl> attrDeclList = relDecl.getAttrDecls();
    assert index < attrDeclList.size() : "index " + index + " is out of bound";
    return attrDeclList.get(index);
  }

}
