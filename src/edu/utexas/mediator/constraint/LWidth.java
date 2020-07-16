package edu.utexas.mediator.constraint;

public class LWidth extends Constraint {

  private String relation;
  private int width;

  protected LWidth(String relation, int width) {
    this.relation = relation;
    this.width = width;
  }

  public String getRelation() {
    return relation;
  }

  public int getWidth() {
    return width;
  }

  @Override
  public String toString() {
    return "width(" + relation + ") = " + width;
  }

  @Override
  public int hashCode() {
    return relation.hashCode() + width;
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) return true;
    if (o instanceof LWidth) {
      return relation.equals(((LWidth) o).relation) && width == ((LWidth) o).width;
    }
    return false;
  }

  @Override
  public Constraint substitute(LTerm from, LTerm to) {
    return this;
  }

}
