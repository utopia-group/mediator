package edu.utexas.mediator.constraint;

import java.util.LinkedHashMap;
import java.util.Map;

import edu.utexas.mediator.ast.AttrDecl;
import edu.utexas.mediator.ast.RelDecl;

public class LRelConst extends LTerm {

  private final String name;
  private final RelDecl relDecl;

  protected LRelConst(RelDecl relDecl) {
    super(assignAttrIndices(relDecl));
    this.name = relDecl.getName();
    this.relDecl = relDecl;
  }

  public String getName() {
    return name;
  }

  public RelDecl getRelDecl() {
    return relDecl;
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
    if (o instanceof LRelConst) {
      return name.equals(((LRelConst) o).name);
    }
    return false;
  }

  private static Map<String, Integer> assignAttrIndices(RelDecl relDecl) {
    Map<String, Integer> map = new LinkedHashMap<>();
    int index = 0;
    for (AttrDecl attrDecl : relDecl.getAttrDecls()) {
      map.put(attrDecl.getName(), index);
      ++index;
    }
    return map;
  }

  @Override
  public LTerm substitute(LTerm from, LTerm to) {
    if (this.equals(from)) return to;
    return this;
  }

}
