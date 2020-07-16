package edu.utexas.mediator.constraint;

import java.util.Map;

public abstract class LTerm extends Constraint {

  protected Map<String, Integer> attrIndices;

  /**
   * All subtypes of LTerm must provide a map from attributes
   * to their corresponding integer indices
   * 
   * @param attrIndices
   *          the map
   */
  public LTerm(Map<String, Integer> attrIndices) {
    this.attrIndices = attrIndices;
  }

  /**
   * Get the map from attributes to indices
   * 
   * @return the map
   */
  public Map<String, Integer> getAttrIndices() {
    return attrIndices;
  }

}
