package edu.utexas.mediator.ast;

public class EquiJoinNode extends AstTerm {

  private AstTerm lhs;
  private AstTerm rhs;
  private String leftAttr;
  private String rightAttr;

  public EquiJoinNode(AstTerm lhs, AstTerm rhs) {
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

  public String getLeftAttr() {
    return leftAttr;
  }

  public void setLeftAttr(String leftAttr) {
    this.leftAttr = leftAttr;
  }

  public String getRightAttr() {
    return rightAttr;
  }

  public void setRightAttr(String rightAttr) {
    this.rightAttr = rightAttr;
  }

  @Override
  public String toString() {
    return "join(" + lhs + "," + rhs + ")";
  }

  @Override
  public int hashCode() {
    return (2 * lhs.hashCode()) ^ (3 * rhs.hashCode());
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) return true;
    if (o instanceof EquiJoinNode) {
      return lhs.equals(((EquiJoinNode) o).lhs) && rhs.equals(((EquiJoinNode) o).rhs);
    }
    return false;
  }

  @Override
  public void accept(IVisitor visitor) {
    visitor.visit(this);
  }

}
