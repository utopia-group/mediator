package edu.utexas.mediator.ast;

import java.util.List;

public class TupleNode extends AstNode {

  private List<String> values;

  public TupleNode(List<String> values) {
    this.values = values;
  }

  public List<String> getValues() {
    return values;
  }

  public void setValues(List<String> values) {
    this.values = values;
  }

  @Override
  public String toString() {
    return values.toString().replace('[', '(').replace(']', ')');
  }

  @Override
  public int hashCode() {
    int hash = 1;
    for (String value : values) {
      hash = hash * 31 + value.hashCode();
    }
    return hash;
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) return true;
    if (o instanceof TupleNode) {
      return values.equals(((TupleNode) o).values);
    }
    return false;
  }

  @Override
  public void accept(IVisitor visitor) {
    visitor.visit(this);
  }

}
