package edu.utexas.mediator.ast;

public class InPredNode extends Predicate {

  private String lhs;
  private AstTerm rhsExpr;

  public InPredNode(String lhs, AstTerm rhsExpr) {
    this.lhs = lhs;
    this.rhsExpr = rhsExpr;
  }

  public String getLhs() {
    return lhs;
  }

  public void setLhs(String lhs) {
    this.lhs = lhs;
  }

  public AstTerm getRhsExpr() {
    return rhsExpr;
  }

  public void setRhsExpr(AstTerm rhsExpr) {
    this.rhsExpr = rhsExpr;
  }

  @Override
  public String toString() {
    return "in(" + lhs + "," + rhsExpr + ")";
  }

  @Override
  public int hashCode() {
    return lhs.hashCode() ^ (7 * rhsExpr.hashCode());
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) return true;
    if (o instanceof InPredNode) {
      return lhs.equals(((InPredNode) o).lhs) && rhsExpr.equals(((InPredNode) o).rhsExpr);
    }
    return false;
  }

  @Override
  public void accept(IVisitor visitor) {
    visitor.visit(this);
  }

}
