package edu.utexas.mediator.solver;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Graph {
  private Map<String, RelNode> rels;

  public Graph() {
    rels = new LinkedHashMap<>();
  }

  public List<RelNode> getRels() {
    return new ArrayList<>(rels.values());
  }

  public boolean containsRel(String name) {
    return rels.containsKey(name);
  }

  public RelNode findRelByName(String name) {
    assert rels.containsKey(name);
    return rels.get(name);
  }

  public void addRel(RelNode rel) {
    assert !rels.containsKey(rel.getName()) : rel + " is duplicated relation in: " + rels;
    rels.put(rel.getName(), rel);
  }

  public void addRelOrReplace(RelNode rel) {
    rels.put(rel.getName(), rel);
  }

  public void removeRel(String name) {
    assert rels.containsKey(name);
    rels.remove(name);
  }

  @Override
  public String toString() {
    return rels.toString();
  }

  @Override
  public int hashCode() {
    return rels.hashCode();
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) return true;
    if (o instanceof Graph) {
      return rels.equals(((Graph) o).rels);
    }
    return false;
  }

}
