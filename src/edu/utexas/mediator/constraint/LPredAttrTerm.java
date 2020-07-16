package edu.utexas.mediator.constraint;

public class LPredAttrTerm extends LPred {

  private final LAttrConst attribute;
  private final LTerm relation;

  protected LPredAttrTerm(LAttrConst attribute, LTerm relation) {
    this.attribute = attribute;
    this.relation = relation;
  }

  public LAttrConst getAttribute() {
    return attribute;
  }

  public LTerm getRelation() {
    return relation;
  }

  @Override
  public String toString() {
    return "in(" + attribute + "," + relation + ")";
  }

  @Override
  public int hashCode() {
    return attribute.hashCode() ^ relation.hashCode();
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) return true;
    if (o instanceof LPredAttrTerm) {
      return attribute.equals(((LPredAttrTerm) o).attribute)
          && relation.equals(((LPredAttrTerm) o).relation);
    }
    return false;
  }

  @Override
  public LPredAttrTerm substitute(LTerm from, LTerm to) {
    return new LPredAttrTerm(attribute, (LTerm) relation.substitute(from, to));
  }

}
