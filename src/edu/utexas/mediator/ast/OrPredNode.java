package edu.utexas.mediator.ast;

public class OrPredNode extends Predicate {

  private Predicate lhs;
  private Predicate rhs;

  public OrPredNode(Predicate lhs, Predicate rhs) {
    this.lhs = lhs;
    this.rhs = rhs;
  }

  public Predicate getLhs() {
    return lhs;
  }

  public void setLhs(Predicate lhs) {
    this.lhs = lhs;
  }

  public Predicate getRhs() {
    return rhs;
  }

  public void setRhs(Predicate rhs) {
    this.rhs = rhs;
  }

  @Override
  public String toString() {
    return "or(" + lhs + "," + rhs + ")";
  }

  @Override
  public int hashCode() {
    return lhs.hashCode() | rhs.hashCode();
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) return true;
    if (o instanceof OrPredNode) {
      return lhs.equals(((OrPredNode) o).lhs) && rhs.equals(((OrPredNode) o).rhs);
    }
    return false;
  }

  @Override
  public void accept(IVisitor visitor) {
    visitor.visit(this);
  }

}
