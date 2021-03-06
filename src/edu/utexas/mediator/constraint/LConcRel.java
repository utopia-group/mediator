package edu.utexas.mediator.constraint;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import edu.utexas.mediator.ast.AttrDecl;

public class LConcRel extends LTerm {

  private final String name;
  private final List<AttrDecl> attrDecls;
  private final List<List<String>> values;

  protected LConcRel(String name, List<AttrDecl> attrDecls) {
    super(assignAttrIndices(attrDecls));
    this.name = name;
    this.attrDecls = attrDecls;
    this.values = new ArrayList<>();
  }

  public String getName() {
    return name;
  }

  public List<AttrDecl> getAttrDecls() {
    return attrDecls;
  }

  public List<List<String>> getValues() {
    return values;
  }

  public void addTuple(List<String> tuple) {
    values.add(tuple);
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
    if (o instanceof LConcRel) {
      return name.equals(((LConcRel) o).name);
    }
    return false;
  }

  private static Map<String, Integer> assignAttrIndices(List<AttrDecl> attrDecls) {
    Map<String, Integer> map = new LinkedHashMap<>();
    int index = 0;
    for (AttrDecl attrDecl : attrDecls) {
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
