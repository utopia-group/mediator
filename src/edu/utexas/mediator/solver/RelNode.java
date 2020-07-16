package edu.utexas.mediator.solver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.utexas.mediator.ast.RelDecl;

public class RelNode {
  private final String name;
  private final RelDecl relDecl;
  private Map<String, AttrNode> attrs;

  public RelNode(String name, RelDecl relDecl) {
    this.name = name;
    this.relDecl = relDecl;
    attrs = new HashMap<>();
  }

  public String getName() {
    return name;
  }

  public RelDecl getRelDecl() {
    return relDecl;
  }

  public boolean containsAttr(String name) {
    return attrs.containsKey(name);
  }

  public AttrNode findAttrByName(String name) {
    return attrs.get(name);
  }

  public List<AttrNode> getAttrs() {
    return new ArrayList<>(attrs.values());
  }

  public void addAttr(AttrNode attr) {
    assert !attrs.containsKey(attr.getName());
    attrs.put(attr.getName(), attr);
  }

  @Override
  public String toString() {
    return attrs.toString();
  }

  @Override
  public int hashCode() {
    return name.hashCode();
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) return true;
    if (o instanceof RelNode) {
      return name.equals(((RelNode) o).name);
    }
    return false;
  }

}
