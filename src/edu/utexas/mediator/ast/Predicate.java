package edu.utexas.mediator.ast;

public abstract class Predicate extends AstNode {

  @Override
  public void accept(IVisitor visitor) {
    if (this instanceof LopPredNode) {
      ((LopPredNode) this).accept(visitor);
    } else if (this instanceof InPredNode) {
      ((InPredNode) this).accept(visitor);
    } else if (this instanceof AndPredNode) {
      ((AndPredNode) this).accept(visitor);
    } else if (this instanceof OrPredNode) {
      ((OrPredNode) this).accept(visitor);
    } else if (this instanceof NotPredNode) {
      ((NotPredNode) this).accept(visitor);
    } else {
      throw new RuntimeException("Unknown subtype of Predicate");
    }
  }

}
