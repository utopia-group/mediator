package edu.utexas.mediator.constraint;

public abstract class Constraint {

  /**
   * Substitute all occurrences of sub-term `from' with sub-term `to'
   * 
   * @param from
   *          original sub-term
   * @param to
   *          new sub-term
   * @return new constraint
   */
  public abstract Constraint substitute(LTerm from, LTerm to);

}
