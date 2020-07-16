package edu.utexas.mediator.ast;

import java.util.List;

public class AttrListNode extends AstNode {

  private List<String> attributes;

  public AttrListNode(List<String> attributes) {
    this.attributes = attributes;
  }

  public List<String> getAttributes() {
    return attributes;
  }

  public void setAttrList(List<String> attributes) {
    this.attributes = attributes;
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
    if (o instanceof AttrListNode) {
      return attributes.equals(((AttrListNode) o).attributes);
    }
    return false;
  }

  @Override
  public void accept(IVisitor visitor) {
    visitor.visit(this);
  }

}
