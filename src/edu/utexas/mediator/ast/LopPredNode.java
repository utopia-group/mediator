package edu.utexas.mediator.ast;

import edu.utexas.mediator.Enumerations;
import edu.utexas.mediator.Enumerations.Operator;

public class LopPredNode extends Predicate {

  private Operator op;
  private String lhs;
  private String rhs;

  public LopPredNode(String op, String lhs, String rhs) {
    this.lhs = lhs;
    this.rhs = rhs;
    this.op = Enumerations.stringToOperator(op);
  }

  public Operator getOp() {
    return op;
  }

  public void setOp(Operator op) {
    this.op = op;
  }

  public String getLhs() {
    return lhs;
  }

  public void setLhs(String lhs) {
    this.lhs = lhs;
  }

  public String getRhs() {
    return rhs;
  }

  public void setRhs(String rhs) {
    this.rhs = rhs;
  }

  @Override
  public String toString() {
    return lhs + Enumerations.operatorToString(op) + rhs;
  }

  @Override
  public int hashCode() {
    return (lhs.hashCode() ^ rhs.hashCode()) + Enumerations.operatorToString(op).hashCode();
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) return true;
    if (o instanceof LopPredNode) {
      return lhs.equals(((LopPredNode) o).lhs) && rhs.equals(((LopPredNode) o).rhs)
          && op == ((LopPredNode) o).op;
    }
    return false;
  }

  @Override
  public void accept(IVisitor visitor) {
    visitor.visit(this);
  }

}
