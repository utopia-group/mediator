package edu.utexas.mediator.ast;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Schema implements IVisitable {

  private List<RelDecl> relDecls;
  private Map<String, RelDecl> lookup;

  public Schema(List<RelDecl> relDecls) {
    this.relDecls = relDecls;
    lookup = buildLookupTable(relDecls);
  }

  public boolean containsRelName(String name) {
    return lookup.containsKey(name);
  }

  public RelDecl getRelDeclByName(String name) {
    assert lookup.containsKey(name) : "Unknown relation: " + name;
    return lookup.get(name);
  }

  public List<RelDecl> getRelDecls() {
    return relDecls;
  }

  public void setRelDecls(List<RelDecl> relDecls) {
    this.relDecls = relDecls;
    lookup = buildLookupTable(relDecls);
  }

  public Map<String, RelDecl> getLookup() {
    return lookup;
  }

  public void setLookup(Map<String, RelDecl> lookup) {
    this.lookup = lookup;
  }

  @Override
  public String toString() {
    return relDecls.toString();
  }

  @Override
  public int hashCode() {
    return relDecls.hashCode();
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) return true;
    if (o instanceof Schema) {
      return relDecls.equals(((Schema) o).relDecls);
    }
    return false;
  }

  private Map<String, RelDecl> buildLookupTable(List<RelDecl> relDecls) {
    Map<String, RelDecl> map = new HashMap<>();
    for (RelDecl relDecl : relDecls) {
      map.put(relDecl.getName(), relDecl);
    }
    return map;
  }

  @Override
  public void accept(IVisitor visitor) {
    visitor.visit(this);
  }

}
