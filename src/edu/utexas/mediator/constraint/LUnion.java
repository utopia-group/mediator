package edu.utexas.mediator.constraint;

public class LUnion extends LTerm {

  private final LTerm lhs;
  private final LTerm rhs;

  protected LUnion(LTerm lhs, LTerm rhs) {
    super(lhs.attrIndices);
    assert lhs.attrIndices.equals(rhs.attrIndices);
    this.lhs = lhs;
    this.rhs = rhs;
  }

  public LTerm getLhs() {
    return lhs;
  }

  public LTerm getRhs() {
    return rhs;
  }

  @Override
  public String toString() {
    return "union(" + lhs + "," + rhs + ")";
  }

  @Override
  public int hashCode() {
    return lhs.hashCode() + rhs.hashCode();
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) return true;
    if (o instanceof LUnion) {
      return lhs.equals(((LUnion) o).lhs) && rhs.equals(((LUnion) o).rhs);
    }
    return false;
  }

  @Override
  public LTerm substitute(LTerm from, LTerm to) {
    if (this.equals(from)) return to;
    return new LUnion((LTerm) lhs.substitute(from, to), (LTerm) rhs.substitute(from, to));
  }

}
