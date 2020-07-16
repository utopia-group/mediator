package edu.utexas.mediator.constraint;

public class LAnd extends Constraint {

  private final Constraint lhs;
  private final Constraint rhs;

  protected LAnd(Constraint lhs, Constraint rhs) {
    this.lhs = lhs;
    this.rhs = rhs;
  }

  public Constraint getLhs() {
    return lhs;
  }

  public Constraint getRhs() {
    return rhs;
  }

  @Override
  public String toString() {
    return "and(" + lhs + "," + rhs + ")";
  }

  @Override
  public int hashCode() {
    return lhs.hashCode() & rhs.hashCode();
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) return true;
    if (o instanceof LAnd) {
      return lhs.equals(((LAnd) o).lhs) && rhs.equals(((LAnd) o).rhs);
    }
    return false;
  }

  @Override
  public LAnd substitute(LTerm from, LTerm to) {
    return new LAnd(lhs.substitute(from, to), rhs.substitute(from, to));
  }

}
