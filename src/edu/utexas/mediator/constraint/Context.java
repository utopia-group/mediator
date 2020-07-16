package edu.utexas.mediator.constraint;

import java.util.List;

import edu.utexas.mediator.Enumerations.Operator;
import edu.utexas.mediator.ast.AttrDecl;
import edu.utexas.mediator.ast.RelDecl;

public class Context {

  private int varCount = 0;

  public LRelConst mkRelConst(RelDecl relDecl) {
    return new LRelConst(relDecl);
  }

  public LRelVar mkRelVar(String name, List<AttrDecl> attrDecls) {
    return new LRelVar(name, attrDecls);
  }

  public LRelVar mkFreshRelVar(List<AttrDecl> attrDecls) {
    return new LRelVar("var_" + String.valueOf(varCount++), attrDecls);
  }

  public LConcRel mkConcRel(String name, List<AttrDecl> attrDecls) {
    return new LConcRel(name, attrDecls);
  }

  public LProject mkProject(LAttrList attrList, LTerm relation) {
    return new LProject(attrList, relation);
  }

  public LSelect mkSelect(LPred pred, LTerm relation) {
    return new LSelect(pred, relation);
  }

  public LEquiJoin mkEquiJoin(LTerm lhs, LTerm rhs, LAttrConst lattr, LAttrConst rattr) {
    return new LEquiJoin(lhs, rhs, lattr, rattr);
  }

  public LUnion mkUnion(LTerm lhs, LTerm rhs) {
    return new LUnion(lhs, rhs);
  }

  public LMinus mkMinus(LTerm lhs, LTerm rhs) {
    return new LMinus(lhs, rhs);
  }

  public LWrite mkWrite(LTerm relation, LAttrConst attr, LValConst value) {
    return new LWrite(relation, attr, value);
  }

  public LRelNil mkRelNil() {
    return new LRelNil();
  }

  public LPredAttrVal mkPredAttrVal(LAttrConst attr, Operator op, LValConst value) {
    return new LPredAttrVal(attr, op, value);
  }

  public LPredAttrTerm mkPredAttrTerm(LAttrConst attr, LTerm term) {
    return new LPredAttrTerm(attr, term);
  }

  public LEqTerm mkEqTerm(LTerm lhs, LTerm rhs) {
    return new LEqTerm(lhs, rhs);
  }

  public LExists mkExists(LRelVar var, Constraint formula) {
    return new LExists(var, formula);
  }

  public LAnd mkAnd(Constraint lhs, Constraint rhs) {
    return new LAnd(lhs, rhs);
  }

  public LConjunctList mkConjunctList() {
    return new LConjunctList();
  }

  public Invariant mkInvariant() {
    return new Invariant();
  }

  public LImply mkImply(Constraint lhs, Constraint rhs) {
    return new LImply(lhs, rhs);
  }

  public LAttrConst mkAttrConst(String name) {
    return new LAttrConst(name);
  }

  public LValConst mkValConst(String name) {
    return new LValConst(name);
  }

  public LAttrList mkAttrList(List<String> attrs) {
    return new LAttrList(attrs);
  }

  public LWidth mkWidth(String relation, int width) {
    return new LWidth(relation, width);
  }

}
