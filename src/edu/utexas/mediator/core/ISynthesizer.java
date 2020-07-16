package edu.utexas.mediator.core;

import edu.utexas.mediator.ast.Program;
import edu.utexas.mediator.constraint.Invariant;

public interface ISynthesizer {

  /**
   * Return the invariant, which is a set of predicates
   * 
   * @return invariant
   */
  public Invariant synthesize(Program src, Program tgt);

}
