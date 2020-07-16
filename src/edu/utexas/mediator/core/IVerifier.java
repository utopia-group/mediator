package edu.utexas.mediator.core;

import edu.utexas.mediator.ast.Program;

public interface IVerifier {

  /**
   * Verify whether two programs are equivalent
   * 
   * @param src
   *          source program AST
   * @param tgt
   *          target program AST
   * @return true if equivalent, false otherwise
   */
  public boolean verify(Program src, Program tgt);

}
