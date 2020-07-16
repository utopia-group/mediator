package edu.utexas.mediator.constraint;

public class LValConst {

  private final String name;

  protected LValConst(String name) {
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
    if (o instanceof LValConst) {
      return name.equals(((LValConst) o).name);
    }
    return false;
  }

}
