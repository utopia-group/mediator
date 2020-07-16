package edu.utexas.mediator.constraint;

public class LWrite extends LTerm {

  private final LTerm relation;
  private final LAttrConst attr;
  private final LValConst value;

  public LWrite(LTerm relation, LAttrConst attr, LValConst value) {
    super(relation.attrIndices);
    this.relation = relation;
    this.attr = attr;
    this.value = value;
  }

  public LTerm getRelation() {
    return relation;
  }

  public LAttrConst getAttr() {
    return attr;
  }

  public LValConst getValue() {
    return value;
  }

  @Override
  public String toString() {
    return "write(" + relation + "," + attr + "," + value + ")";
  }

  @Override
  public int hashCode() {
    return relation.hashCode() ^ attr.hashCode() ^ value.hashCode();
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) return true;
    if (o instanceof LWrite) {
      return relation.equals(((LWrite) o).relation) && attr.equals(((LWrite) o).attr)
          && value.equals(((LWrite) o).value);
    }
    return false;
  }

  @Override
  public LTerm substitute(LTerm from, LTerm to) {
    if (this.equals(from)) return to;
    return new LWrite((LTerm) relation.substitute(from, to), attr, value);
  }

}
