package edu.utexas.mediator.solver;

public class AttrNode {

  private int index;
  private String name;
  private String label;

  public AttrNode(int index, String name) {
    this.index = index;
    this.name = name;
  }

  public AttrNode(int index, String name, String label) {
    this(index, name);
    this.label = label;
  }

  public int getIndex() {
    return index;
  }

  public String getName() {
    return name;
  }

  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  @Override
  public String toString() {
    return index + ":" + label;
  }

  @Override
  public int hashCode() {
    return name.hashCode();
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) return true;
    if (o instanceof AttrNode) {
      return name.equals(((AttrNode) o).name);
    }
    return false;
  }

}
