package edu.utexas.mediator.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Expr;
import com.microsoft.z3.FuncDecl;
import com.microsoft.z3.ListSort;
import com.microsoft.z3.Sort;

import edu.utexas.mediator.Enumerations.Operator;
import edu.utexas.mediator.ast.AttrDecl;
import edu.utexas.mediator.constraint.Constraint;
import edu.utexas.mediator.constraint.LAnd;
import edu.utexas.mediator.constraint.LAttrConst;
import edu.utexas.mediator.constraint.LAttrList;
import edu.utexas.mediator.constraint.LConcRel;
import edu.utexas.mediator.constraint.LConjunctList;
import edu.utexas.mediator.constraint.LEqTerm;
import edu.utexas.mediator.constraint.LEquiJoin;
import edu.utexas.mediator.constraint.LExists;
import edu.utexas.mediator.constraint.LImply;
import edu.utexas.mediator.constraint.LMinus;
import edu.utexas.mediator.constraint.LPred;
import edu.utexas.mediator.constraint.LPredAttrTerm;
import edu.utexas.mediator.constraint.LPredAttrVal;
import edu.utexas.mediator.constraint.LProject;
import edu.utexas.mediator.constraint.LRelConst;
import edu.utexas.mediator.constraint.LRelNil;
import edu.utexas.mediator.constraint.LRelVar;
import edu.utexas.mediator.constraint.LSelect;
import edu.utexas.mediator.constraint.LTerm;
import edu.utexas.mediator.constraint.LUnion;
import edu.utexas.mediator.constraint.LValConst;
import edu.utexas.mediator.constraint.LWidth;
import edu.utexas.mediator.constraint.LWrite;

public class Z3CstrConverter {

  private final Z3ListSolver z3ListSolver;
  private final com.microsoft.z3.Context z3Ctx;
  private final List<BoolExpr> premises;

  // TODO: this map for cache purpose might not be correct
  // if the same attribute list has different attribute indices
  // for different terms
  private final Map<LAttrList, Expr> listSymbolMap;

  private final Map<LConcRel, Expr> relSymbolMap;

  private final Sort valSort;
  private final ListSort attrListSort;
  private final ListSort tupleSort;
  private final ListSort relSort;

  public Z3CstrConverter(Z3ListSolver z3Solver) {
    z3ListSolver = z3Solver;
    z3Ctx = z3ListSolver.getZ3Context();
    premises = new ArrayList<>();
    listSymbolMap = new HashMap<>();
    relSymbolMap = new HashMap<>();

    valSort = z3ListSolver.getSortByName("Val");
    attrListSort = (ListSort) z3ListSolver.getSortByName("AttrList");
    tupleSort = (ListSort) z3ListSolver.getSortByName("Tuple");
    relSort = (ListSort) z3ListSolver.getSortByName("Relation");
  }

  public BoolExpr transform(Constraint cstr) {
    // call convert first to populate the premises
    BoolExpr conclusion = convert(cstr);

    if (premises.isEmpty()) return conclusion;

    BoolExpr expr = z3Ctx.mkTrue();
    for (BoolExpr premise : premises) {
      expr = z3Ctx.mkAnd(expr, premise);
    }
    return z3Ctx.mkImplies(expr, conclusion);
  }

  public BoolExpr convert(Constraint cstr) {
    if (cstr instanceof LEqTerm) {
      return convert((LEqTerm) cstr);
    } else if (cstr instanceof LConjunctList) {
      return convert((LConjunctList) cstr);
    } else if (cstr instanceof LWidth) {
      return convert((LWidth) cstr);
    } else if (cstr instanceof LAnd) {
      return convert((LAnd) cstr);
    } else if (cstr instanceof LImply) {
      return convert((LImply) cstr);
    } else if (cstr instanceof LExists) {
      return convert((LExists) cstr);
    } else {
      throw new RuntimeException("Unknown Constraint subtype");
    }
  }

  public Expr convert(LTerm term) {
    if (term instanceof LRelConst) {
      return convert((LRelConst) term);
    } else if (term instanceof LRelVar) {
      return convert((LRelVar) term);
    } else if (term instanceof LProject) {
      return convert((LProject) term);
    } else if (term instanceof LSelect) {
      return convert((LSelect) term);
    } else if (term instanceof LEquiJoin) {
      return convert((LEquiJoin) term);
    } else if (term instanceof LUnion) {
      return convert((LUnion) term);
    } else if (term instanceof LMinus) {
      return convert((LMinus) term);
    } else if (term instanceof LWrite) {
      return convert((LWrite) term);
    } else if (term instanceof LRelNil) {
      return convert((LRelNil) term);
    } else if (term instanceof LConcRel) {
      return convert((LConcRel) term);
    } else {
      throw new RuntimeException("Unknown LTerm subtype");
    }
  }

  public BoolExpr convert(LEqTerm eq) {
    return z3Ctx.mkEq(convert(eq.getLhs()), convert(eq.getRhs()));
  }

  public BoolExpr convert(LAnd land) {
    return z3Ctx.mkAnd(convert(land.getLhs()), convert(land.getRhs()));
  }

  public BoolExpr convert(LImply imply) {
    return z3Ctx.mkImplies(convert(imply.getLhs()), convert(imply.getRhs()));
  }

  public BoolExpr convert(LExists exists) {
    Expr[] bound = new Expr[1];
    bound[0] = convert(exists.getVariable());
    Expr body = convert(exists.getFormula());
    return z3Ctx.mkExists(bound, body, 1, null, null, null, null);
  }

  public Expr convert(LRelConst rel) {
    return z3Ctx.mkConst(rel.getName(), relSort);
  }

  public Expr convert(LRelVar var) {
    return z3Ctx.mkConst(var.getName(), relSort);
  }

  public Expr convert(LProject proj) {
    FuncDecl funcProj = getFunctionByName("proj");
    LTerm term = proj.getRelation();
    return funcProj.apply(convert(proj.getAttrList(), term.getAttrIndices()), convert(term));
  }

  public Expr convert(LSelect sel) {
    LPred pred = sel.getPred();
    if (pred instanceof LPredAttrVal) {
      return convertSelectAttrVal(sel);
    } else if (pred instanceof LPredAttrTerm) {
      return convertSelectAttrTerm(sel);
    } else {
      throw new RuntimeException("Unknown predicate type in selection");
    }
  }

  private Expr convertSelectAttrVal(LSelect sel) {
    assert sel.getPred() instanceof LPredAttrVal;
    FuncDecl funcSelEqv = getFunctionByName("sel-eqv");
    LPredAttrVal pred = (LPredAttrVal) sel.getPred();
    LAttrConst attr = pred.getAttribute();
    LValConst val = pred.getValue();
    LTerm term = sel.getRelation();
    return funcSelEqv.apply(convert(attr, term.getAttrIndices()), convert(val), convert(term));
  }

  private Expr convertSelectAttrTerm(LSelect sel) {
    // TODO super restrictive - lots of assertions
    assert sel.getPred() instanceof LPredAttrTerm;
    FuncDecl funcSelInEqv = getFunctionByName("sel-in-eqv");
    LPredAttrTerm pred = (LPredAttrTerm) sel.getPred();
    LAttrConst pk = pred.getAttribute();
    assert pred.getRelation() instanceof LProject;
    LProject proj = (LProject) pred.getRelation();
    assert proj.getAttrList().getAttrNum() == 1;
    String fk = proj.getAttrList().getAttributes().get(0);
    assert proj.getRelation() instanceof LSelect;
    LSelect innerSel = (LSelect) proj.getRelation();
    // TODO only support sel-eqv currently
    assert innerSel.getPred() instanceof LPredAttrVal;
    LPredAttrVal innerPred = (LPredAttrVal) innerSel.getPred();
    assert innerPred.getOperator() == Operator.EQ;

    List<AttrDecl> attrDecls;
    if (innerSel.getRelation() instanceof LRelConst) {
      attrDecls = ((LRelConst) innerSel.getRelation()).getRelDecl().getAttrDecls();
    } else if (innerSel.getRelation() instanceof LRelVar) {
      attrDecls = ((LRelVar) innerSel.getRelation()).getAttrDecls();
    } else {
      throw new RuntimeException("Unknown inner term type");
    }
    AttrDecl fkDecl = attrDecls.stream().filter((attr) -> attr.isForeignKey()
        && attr.getName().equals(fk) && attr.getReferenceAttrDecl().getName().equals(pk.getName()))
        .findFirst().orElse(null);
    assert fkDecl != null;
    return funcSelInEqv.apply(
        convert(innerPred.getAttribute(), innerSel.getRelation().getAttrIndices()),
        convert(innerPred.getValue()), convert(innerSel.getRelation()), convert(sel.getRelation()));
  }

  public Expr convert(LEquiJoin join) {
    FuncDecl funcEquiJoin = getFunctionByName("equi-join");
    // Note that the attribute indices are determined after join
    // because it is a syntactic sugar of prod and sel-eq
    return funcEquiJoin.apply(convert(join.getLeftAttr(), join.getAttrIndices()),
        convert(join.getRightAttr(), join.getAttrIndices()), convert(join.getLhs()),
        convert(join.getRhs()));
  }

  public Expr convert(LUnion union) {
    FuncDecl funcAppend = getFunctionByName("append");
    return funcAppend.apply(convert(union.getLhs()), convert(union.getRhs()));
  }

  public Expr convert(LMinus minus) {
    FuncDecl funcMinus = getFunctionByName("minus");
    return funcMinus.apply(convert(minus.getLhs()), convert(minus.getRhs()));
  }

  public Expr convert(LWrite upd) {
    FuncDecl funcUpd = getFunctionByName("upd");
    LTerm term = upd.getRelation();
    Expr attr = convert(upd.getAttr(), term.getAttrIndices());
    return funcUpd.apply(convert(term), attr, convert(upd.getValue()));
  }

  public Expr convert(LConcRel rel) {
    if (relSymbolMap.containsKey(rel)) return relSymbolMap.get(rel);

    Expr symbol = z3Ctx.mkFreshConst("cr", relSort);
    Expr expr = relSort.getNil();
    for (int i = rel.getValues().size() - 1; i >= 0; --i) {
      List<String> tuple = rel.getValues().get(i);
      Expr tupleExpr = tupleSort.getNil();
      for (int j = tuple.size() - 1; j >= 0; --j) {
        tupleExpr = tupleSort.getConsDecl().apply(z3Ctx.mkConst(tuple.get(j), valSort), tupleExpr);
      }
      expr = relSort.getConsDecl().apply(tupleExpr, expr);
    }
    BoolExpr relDef = z3Ctx.mkEq(symbol, expr);
    // put the relation definition in the premises list and relation symbol map
    premises.add(relDef);
    relSymbolMap.put(rel, symbol);
    return symbol;
  }

  public Expr convert(LRelNil nil) {
    return relSort.getNil();
  }

  public Expr convert(LAttrList attrlist, Map<String, Integer> attrIndices) {
    // get the list symbol directly if defined previously
    if (listSymbolMap.containsKey(attrlist)) return listSymbolMap.get(attrlist);

    Expr expr = attrListSort.getNil();
    List<String> attrs = attrlist.getAttributes();
    for (int i = attrs.size() - 1; i >= 0; --i) {
      assert attrIndices.containsKey(attrs.get(i));
      expr = attrListSort.getConsDecl().apply(z3Ctx.mkInt(attrIndices.get(attrs.get(i))), expr);
    }
    Expr symbol = z3Ctx.mkFreshConst("l", attrListSort);
    BoolExpr listDef = z3Ctx.mkEq(symbol, expr);
    listSymbolMap.put(attrlist, symbol);
    // put the list definition in the premises list and length-list map
    premises.add(listDef);
    return symbol;
  }

  public Expr convert(LAttrConst attr, Map<String, Integer> attrIndices) {
    assert attrIndices.containsKey(attr.getName()) : "Unknown attribute: " + attr;
    return buildAttrConst(attrIndices.get(attr.getName()));
  }

  private Expr buildAttrConst(int num) {
    return z3Ctx.mkInt(num);
  }

  public Expr convert(LValConst val) {
    return z3Ctx.mkConst(val.getName(), valSort);
  }

  public BoolExpr convert(LConjunctList conjList) {
    BoolExpr expr = z3Ctx.mkTrue();
    for (Constraint conj : conjList.getConjuncts()) {
      expr = z3Ctx.mkAnd(expr, convert(conj));
    }
    return expr;
  }

  public BoolExpr convert(LWidth wid) {
    FuncDecl funcWidth = getFunctionByName("width");
    return z3Ctx.mkEq(funcWidth.apply(z3Ctx.mkConst(wid.getRelation(), relSort)),
        z3Ctx.mkInt(wid.getWidth()));
  }

  private FuncDecl getFunctionByName(String name) {
    return z3ListSolver.getFunctionByName(name);
  }

}
