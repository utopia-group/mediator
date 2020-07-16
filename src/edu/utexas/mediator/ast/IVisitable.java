package edu.utexas.mediator.ast;

public interface IVisitable {

  /**
   * Method that accepts the visitor
   * 
   * @param visitor
   *          the visitor
   */
  public void accept(IVisitor visitor);

}
