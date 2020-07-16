package edu.utexas.mediator.ast;

public class MinusNode extends AstTerm {

  private AstTerm lhs;
  private AstTerm rhs;

  public MinusNode(AstTerm lhs, AstTerm rhs) {
    this.lhs = lhs;
    this.rhs = rhs;
  }

  public AstTerm getLhs() {
    return lhs;
  }

  public void setLhs(AstTerm lhs) {
    this.lhs = lhs;
  }

  public AstTerm getRhs() {
    return rhs;
  }

  public void setRhs(AstTerm rhs) {
    this.rhs = rhs;
  }

  @Override
  public String toString() {
    return "minus(" + lhs + "," + rhs + ")";
  }

  @Override
  public int hashCode() {
    return (5 * lhs.hashCode()) ^ (7 * rhs.hashCode());
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) return true;
    if (o instanceof MinusNode) {
      return lhs.equals(((MinusNode) o).lhs) && rhs.equals(((MinusNode) o).rhs);
    }
    return false;
  }

  @Override
  public void accept(IVisitor visitor) {
    visitor.visit(this);
  }

}
