package edu.utexas.mediator.constraint;

public class LAttrConst {

  private final String name;

  protected LAttrConst(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  @Override
  public String toString() {
    return name;
  }

  @Override
  public int hashCode() {
    return name.hashCode();
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) return true;
    if (o instanceof LAttrConst) {
      return name.equals(((LAttrConst) o).name);
    }
    return false;
  }

}
