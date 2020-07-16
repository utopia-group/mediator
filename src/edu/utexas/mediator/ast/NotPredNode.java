package edu.utexas.mediator.ast;

public class NotPredNode extends Predicate {

  private Predicate pred;

  public NotPredNode(Predicate pred) {
    this.pred = pred;
  }

  public Predicate getPred() {
    return pred;
  }

  public void setPred(Predicate pred) {
    this.pred = pred;
  }

  @Override
  public String toString() {
    return "not(" + pred + ")";
  }

  @Override
  public int hashCode() {
    return ~pred.hashCode();
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) return true;
    if (o instanceof NotPredNode) {
      return pred.equals(((NotPredNode) o).pred);
    }
    return false;
  }

  @Override
  public void accept(IVisitor visitor) {
    visitor.visit(this);
  }

}
