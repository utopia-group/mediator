package edu.utexas.mediator.constraint;

import java.util.List;

public class LAttrList {

  private final List<String> attributes;

  protected LAttrList(List<String> attributes) {
    this.attributes = attributes;
  }

  public List<String> getAttributes() {
    return attributes;
  }

  public int getAttrNum() {
    return attributes.size();
  }

  @Override
  public String toString() {
    return attributes.toString();
  }

  @Override
  public int hashCode() {
    return attributes.hashCode();
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) return true;
    if (o instanceof LAttrList) {
      return attributes.equals(((LAttrList) o).attributes);
    }
    return false;
  }

}
