package edu.utexas.mediator.ast;

public abstract class AstTerm extends AstNode {

  @Override
  public void accept(IVisitor visitor) {
    if (this instanceof RelationNode) {
      ((RelationNode) this).accept(visitor);
    } else if (this instanceof ProjectNode) {
      ((ProjectNode) this).accept(visitor);
    } else if (this instanceof SelectNode) {
      ((SelectNode) this).accept(visitor);
    } else if (this instanceof EquiJoinNode) {
      ((EquiJoinNode) this).accept(visitor);
    } else if (this instanceof UnionNode) {
      ((UnionNode) this).accept(visitor);
    } else if (this instanceof MinusNode) {
      ((MinusNode) this).accept(visitor);
    } else {
      throw new RuntimeException("Unknown AstTerm subtype");
    }
  }

}
