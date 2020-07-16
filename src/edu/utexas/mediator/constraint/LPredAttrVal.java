package edu.utexas.mediator.constraint;

import edu.utexas.mediator.Enumerations;
import edu.utexas.mediator.Enumerations.Operator;

public class LPredAttrVal extends LPred {

  private final LAttrConst attribute;
  private final LValConst value;
  private final Operator op;

  protected LPredAttrVal(LAttrConst attribute, Operator op, LValConst value) {
    this.attribute = attribute;
    this.op = op;
    this.value = value;
  }

  public LAttrConst getAttribute() {
    return attribute;
  }

  public Operator getOperator() {
    return op;
  }

  public LValConst getValue() {
    return value;
  }

  @Override
  public String toString() {
    return attribute + Enumerations.operatorToString(op) + value;
  }

  @Override
  public int hashCode() {
    return attribute.hashCode() ^ value.hashCode() + Enumerations.operatorToString(op).hashCode();
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) return true;
    if (o instanceof LPredAttrVal) {
      return op == ((LPredAttrVal) o).op && attribute.equals(((LPredAttrVal) o).attribute)
          && value.equals(((LPredAttrVal) o).value);
    }
    return false;
  }

  @Override
  public Constraint substitute(LTerm from, LTerm to) {
    return this;
  }

}
