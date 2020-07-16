package edu.utexas.mediator.ast;

public class UnionNode extends AstTerm {

  private AstTerm lhs;
  private AstTerm rhs;

  public UnionNode(AstTerm lhs, AstTerm rhs) {
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
    return "cup(" + lhs + "," + rhs + ")";
  }

  @Override
  public int hashCode() {
    return (11 * lhs.hashCode()) ^ (13 * rhs.hashCode());
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) return true;
    if (o instanceof UnionNode) {
      return lhs.equals(((UnionNode) o).lhs) && rhs.equals(((UnionNode) o).rhs);
    }
    return false;
  }

  @Override
  public void accept(IVisitor visitor) {
    visitor.visit(this);
  }

}
