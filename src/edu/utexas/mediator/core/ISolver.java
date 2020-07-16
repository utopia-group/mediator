package edu.utexas.mediator.core;

import edu.utexas.mediator.constraint.Constraint;

public interface ISolver {

  /**
   * Check a logical formula is VALID
   * 
   * @param formula
   * @return true if VALID, false otherwise
   */
  public boolean isValid(Constraint formula);

}
