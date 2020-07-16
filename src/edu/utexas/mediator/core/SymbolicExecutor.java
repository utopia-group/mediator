package edu.utexas.mediator.core;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.utexas.mediator.ast.AndPredNode;
import edu.utexas.mediator.ast.AstNode;
import edu.utexas.mediator.ast.AstTerm;
import edu.utexas.mediator.ast.AttrListNode;
import edu.utexas.mediator.ast.DeleteNode;
import edu.utexas.mediator.ast.EquiJoinNode;
import edu.utexas.mediator.ast.InPredNode;
import edu.utexas.mediator.ast.InsertNode;
import edu.utexas.mediator.ast.LopPredNode;
import edu.utexas.mediator.ast.MinusNode;
import edu.utexas.mediator.ast.NotPredNode;
import edu.utexas.mediator.ast.OrPredNode;
import edu.utexas.mediator.ast.Predicate;
import edu.utexas.mediator.ast.ProjectNode;
import edu.utexas.mediator.ast.RelDecl;
import edu.utexas.mediator.ast.RelationNode;
import edu.utexas.mediator.ast.SelectNode;
import edu.utexas.mediator.ast.Transaction;
import edu.utexas.mediator.ast.TupleNode;
import edu.utexas.mediator.ast.UnionNode;
import edu.utexas.mediator.ast.UpdateNode;
import edu.utexas.mediator.constraint.Constraint;
import edu.utexas.mediator.constraint.Context;
import edu.utexas.mediator.constraint.LAttrList;
import edu.utexas.mediator.constraint.LConcRel;
import edu.utexas.mediator.constraint.LPred;
import edu.utexas.mediator.constraint.LRelVar;
import edu.utexas.mediator.constraint.LTerm;
import edu.utexas.mediator.constraint.LWidth;

public class SymbolicExecutor {

  private final Context ctx;
  private final Map<RelDecl, LConcRel> relAppendix;

  public SymbolicExecutor(Context ctx) {
    this.ctx = ctx;
    this.relAppendix = new HashMap<>();
  }

  public Context getContext() {
    return ctx;
  }

  public Constraint execute(Constraint cstr, Transaction tran) {
    Constraint result = cstr;
    for (AstNode stmt : tran.getStatements()) {
      result = execute(result, stmt);
    }
    return result;
  }

  public Constraint execute(Constraint cstr, AstNode stmt) {
    if (stmt instanceof InsertNode) {
      return execute(cstr, (InsertNode) stmt);
    } else if (stmt instanceof DeleteNode) {
      return execute(cstr, (DeleteNode) stmt);
    } else if (stmt instanceof UpdateNode) {
      return execute(cstr, (UpdateNode) stmt);
    } else {
      throw new RuntimeException("Unknown subtype of AstNode");
    }
  }

  public LTerm execute(AstTerm term) {
    if (term instanceof RelationNode) {
      return execute((RelationNode) term);
    } else if (term instanceof ProjectNode) {
      return execute((ProjectNode) term);
    } else if (term instanceof SelectNode) {
      return execute((SelectNode) term);
    } else if (term instanceof EquiJoinNode) {
      return execute((EquiJoinNode) term);
    } else if (term instanceof UnionNode) {
      return execute((UnionNode) term);
    } else if (term instanceof MinusNode) {
      return execute((MinusNode) term);
    } else {
      throw new RuntimeException("Unknown subtype of AstTerm");
    }
  }

  public LPred execute(Predicate pred) {
    if (pred instanceof LopPredNode) {
      return execute((LopPredNode) pred);
    } else if (pred instanceof InPredNode) {
      return execute((InPredNode) pred);
    } else if (pred instanceof AndPredNode) {
      return execute((AndPredNode) pred);
    } else if (pred instanceof OrPredNode) {
      return execute((OrPredNode) pred);
    } else if (pred instanceof NotPredNode) {
      return execute((NotPredNode) pred);
    } else {
      throw new RuntimeException("Unknown subtype of Predicate");
    }
  }

  public Constraint execute(Constraint cstr, InsertNode ins) {
    // TODO - only checks concrete invariants currently
    RelDecl rel = ins.getRelation().getRelDecl();
    if (relAppendix.containsKey(rel)) {
      List<String> tuple = execute(ins.getTuple());
      assert tuple.size() == rel.getAttrNum();
      relAppendix.get(rel).addTuple(tuple);
      return cstr;
    }
    LConcRel appendix = ctx.mkConcRel(rel.getName() + "_a", rel.getAttrDecls());
    Constraint substituted = cstr.substitute(ctx.mkRelConst(rel), appendix);
    List<String> tuple = execute(ins.getTuple());
    assert tuple.size() == rel.getAttrDecls().size() : tuple + " is of wrong size";
    appendix.addTuple(tuple);
    relAppendix.put(rel, appendix);
    return substituted;
  }

  public Constraint execute(Constraint cstr, DeleteNode del) {
    RelDecl rel = del.getRelation().getRelDecl();
    LRelVar var = ctx.mkFreshRelVar(rel.getAttrDecls());
    Constraint substituted = cstr.substitute(ctx.mkRelConst(rel), var);
    LPred pred = execute(del.getPred());
    Constraint eq = ctx.mkEqTerm(ctx.mkRelConst(rel), ctx.mkMinus(var, ctx.mkSelect(pred, var)));
    LWidth width = ctx.mkWidth(var.getName(), rel.getAttrNum());
    return ctx.mkExists(var, ctx.mkAnd(ctx.mkAnd(width, eq), substituted));
  }

  public Constraint execute(Constraint cstr, UpdateNode upd) {
    RelDecl rel = upd.getRelation().getRelDecl();
    LRelVar var = ctx.mkFreshRelVar(rel.getAttrDecls());
    Constraint substituted = cstr.substitute(ctx.mkRelConst(rel), var);
    LPred pred = execute(upd.getPred());
    Constraint eq = ctx.mkEqTerm(ctx.mkRelConst(rel),
        ctx.mkUnion(ctx.mkMinus(var, ctx.mkSelect(pred, var)), ctx.mkWrite(ctx.mkSelect(pred, var),
            ctx.mkAttrConst(upd.getAttr()), ctx.mkValConst(upd.getValue()))));
    LWidth width = ctx.mkWidth(var.getName(), rel.getAttrNum());
    return ctx.mkExists(var, ctx.mkAnd(ctx.mkAnd(width, eq), substituted));
  }

  public LTerm execute(RelationNode rel) {
    return ctx.mkRelConst(rel.getRelDecl());
  }

  public LTerm execute(ProjectNode proj) {
    return ctx.mkProject(execute(proj.getAttrList()), execute(proj.getRelation()));
  }

  public LTerm execute(SelectNode sel) {
    return ctx.mkSelect(execute(sel.getPred()), execute(sel.getRelation()));
  }

  public LTerm execute(EquiJoinNode join) {
    return ctx.mkEquiJoin(execute(join.getLhs()), execute(join.getRhs()),
        ctx.mkAttrConst(join.getLeftAttr()), ctx.mkAttrConst(join.getRightAttr()));
  }

  public LTerm execute(UnionNode union) {
    return ctx.mkUnion(execute(union.getLhs()), execute(union.getRhs()));
  }

  public LTerm execute(MinusNode minus) {
    return ctx.mkMinus(execute(minus.getLhs()), execute(minus.getRhs()));
  }

  public LPred execute(LopPredNode lop) {
    return ctx.mkPredAttrVal(ctx.mkAttrConst(lop.getLhs()), lop.getOp(),
        ctx.mkValConst(lop.getRhs()));
  }

  public LPred execute(InPredNode in) {
    return ctx.mkPredAttrTerm(ctx.mkAttrConst(in.getLhs()), execute(in.getRhsExpr()));
  }

  public LPred execute(AndPredNode and) {
    throw new UnsupportedOperationException();
  }

  public LPred execute(OrPredNode or) {
	throw new UnsupportedOperationException();
  }

  public LPred execute(NotPredNode not) {
	throw new UnsupportedOperationException();
  }

  public LAttrList execute(AttrListNode attrlist) {
    return ctx.mkAttrList(attrlist.getAttributes());
  }

  public List<String> execute(TupleNode tuple) {
    return tuple.getValues();
  }

}
