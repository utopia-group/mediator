package edu.utexas.mediator.ast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RelDecl implements IVisitable {

  private String name;
  private List<AttrDecl> attrDecls;
  private Map<String, AttrDecl> lookup;

  public RelDecl() {
  }

  public RelDecl(String name) {
    this.name = name;
    this.attrDecls = new ArrayList<>();
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public boolean containsAttrName(String name) {
    return lookup.containsKey(name);
  }

  public AttrDecl getAttrDeclByName(String name) {
    assert lookup.containsKey(name) : "Unknown attribute: " + name;
    return lookup.get(name);
  }

  public List<AttrDecl> getAttrDecls() {
    return attrDecls;
  }

  public int getAttrNum() {
    return attrDecls.size();
  }

  public void setAttrDecls(List<AttrDecl> attrDecls) {
    this.attrDecls = attrDecls;
    lookup = buildLookupTable(attrDecls);
  }

  public Map<String, AttrDecl> getLookup() {
    return lookup;
  }

  public void setLookup(Map<String, AttrDecl> lookup) {
    this.lookup = lookup;
  }

  @Override
  public String toString() {
    return name + attrDecls.toString().replace('[', '(').replace(']', ')');
  }

  @Override
  public int hashCode() {
    return name.hashCode() + attrDecls.hashCode();
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) return true;
    if (o instanceof RelDecl) {
      return name.equals(((RelDecl) o).name) && attrDecls.equals(((RelDecl) o).attrDecls);
    }
    return false;
  }

  private Map<String, AttrDecl> buildLookupTable(List<AttrDecl> attrDecls) {
    Map<String, AttrDecl> map = new HashMap<>();
    for (AttrDecl attrDecl : attrDecls) {
      map.put(attrDecl.getName(), attrDecl);
    }
    return map;
  }

  @Override
  public void accept(IVisitor visitor) {
    visitor.visit(this);
  }

}
