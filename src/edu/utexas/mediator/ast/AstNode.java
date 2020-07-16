package edu.utexas.mediator.ast;

public abstract class AstNode implements IVisitable {

  @Override
  public void accept(IVisitor visitor) {
    if (this instanceof AstTerm) {
      ((AstTerm) this).accept(visitor);
    } else if (this instanceof Predicate) {
      ((Predicate) this).accept(visitor);
    } else if (this instanceof InsertNode) {
      ((InsertNode) this).accept(visitor);
    } else if (this instanceof DeleteNode) {
      ((DeleteNode) this).accept(visitor);
    } else if (this instanceof UpdateNode) {
      ((UpdateNode) this).accept(visitor);
    } else if (this instanceof AttrListNode) {
      ((AttrListNode) this).accept(visitor);
    } else if (this instanceof TupleNode) {
      ((TupleNode) this).accept(visitor);
    } else {
      throw new RuntimeException("Unknown AstNode subtype");
    }
  }

}
