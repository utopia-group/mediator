package edu.utexas.mediator.constraint;

public class LImply extends Constraint {

  private final Constraint lhs;
  private final Constraint rhs;

  protected LImply(Constraint lhs, Constraint rhs) {
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
    return "imply(" + lhs + "," + rhs + ")";
  }

  @Override
  public int hashCode() {
    return ~lhs.hashCode() | rhs.hashCode();
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) return true;
    if (o instanceof LImply) {
      return lhs.equals(((LImply) o).lhs) && rhs.equals(((LImply) o).rhs);
    }
    return false;
  }

  @Override
  public LImply substitute(LTerm from, LTerm to) {
    return new LImply(lhs.substitute(from, to), rhs.substitute(from, to));
  }

}
