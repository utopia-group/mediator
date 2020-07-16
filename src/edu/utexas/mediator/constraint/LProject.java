package edu.utexas.mediator.constraint;

import java.util.LinkedHashMap;
import java.util.Map;

public class LProject extends LTerm {

  private final LAttrList attrList;
  private final LTerm relation;

  protected LProject(LAttrList attrList, LTerm relation) {
    super(assignAttrIndices(attrList, relation));
    this.attrList = attrList;
    this.relation = relation;
  }

  public LAttrList getAttrList() {
    return attrList;
  }

  public LTerm getRelation() {
    return relation;
  }

  @Override
  public String toString() {
    return "proj(" + attrList + "," + relation + ")";
  }

  @Override
  public int hashCode() {
    return attrList.hashCode() ^ relation.hashCode();
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) return true;
    if (o instanceof LProject) {
      return attrList.equals(((LProject) o).attrList) && relation.equals(((LProject) o).relation);
    }
    return false;
  }

  private static Map<String, Integer> assignAttrIndices(LAttrList attrlist, LTerm term) {
    Map<String, Integer> map = new LinkedHashMap<>();
    int index = 0;
    for (String attr : attrlist.getAttributes()) {
      assert term.attrIndices.containsKey(attr) : "Unknown attribute: " + attr;
      map.put(attr, index);
      ++index;
    }
    return map;
  }

  @Override
  public LTerm substitute(LTerm from, LTerm to) {
    if (this.equals(from)) return to;
    return new LProject(attrList, (LTerm) relation.substitute(from, to));
  }

}
