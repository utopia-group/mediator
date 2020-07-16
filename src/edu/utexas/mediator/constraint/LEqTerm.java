package edu.utexas.mediator.constraint;

public class LEqTerm extends Constraint {

  private final LTerm lhs;
  private final LTerm rhs;

  protected LEqTerm(LTerm lhs, LTerm rhs) {
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
    return "eq(" + lhs + "," + rhs + ")";
  }

  @Override
  public int hashCode() {
    return lhs.hashCode() + rhs.hashCode();
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) return true;
    if (o instanceof LEqTerm) {
      return lhs.equals(((LEqTerm) o).lhs) && rhs.equals(((LEqTerm) o).rhs);
    }
    return false;
  }

  @Override
  public LEqTerm substitute(LTerm from, LTerm to) {
    return new LEqTerm((LTerm) lhs.substitute(from, to), (LTerm) rhs.substitute(from, to));
  }

}
