package edu.utexas.mediator.constraint;

public class LSelect extends LTerm {

  private final LPred pred;
  private final LTerm relation;

  protected LSelect(LPred pred, LTerm relation) {
    super(relation.attrIndices);
    this.pred = pred;
    this.relation = relation;
  }

  public LPred getPred() {
    return pred;
  }

  public LTerm getRelation() {
    return relation;
  }

  @Override
  public String toString() {
    return "sel(" + pred + "," + relation + ")";
  }

  @Override
  public int hashCode() {
    return pred.hashCode() ^ relation.hashCode();
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) return true;
    if (o instanceof LSelect) {
      return pred.equals(((LSelect) o).pred) && relation.equals(((LSelect) o).relation);
    }
    return false;
  }

  @Override
  public LTerm substitute(LTerm from, LTerm to) {
    if (this.equals(from)) return to;
    return new LSelect((LPred) pred.substitute(from, to), (LTerm) relation.substitute(from, to));
  }

}
