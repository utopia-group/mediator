package edu.utexas.mediator.constraint;

import java.util.LinkedHashMap;
import java.util.Map;

public class LEquiJoin extends LTerm {

  private final LTerm lhs;
  private final LTerm rhs;
  private final LAttrConst lattr;
  private final LAttrConst rattr;

  protected LEquiJoin(LTerm lhs, LTerm rhs, LAttrConst lattr, LAttrConst rattr) {
    super(assignAttrIndices(lhs, rhs));
    this.lhs = lhs;
    this.rhs = rhs;
    this.lattr = lattr;
    this.rattr = rattr;
  }

  public LTerm getLhs() {
    return lhs;
  }

  public LTerm getRhs() {
    return rhs;
  }

  public LAttrConst getLeftAttr() {
    return lattr;
  }

  public LAttrConst getRightAttr() {
    return rattr;
  }

  @Override
  public String toString() {
    return "join(" + lhs + "," + rhs + "," + lattr + "," + rattr + ")";
  }

  @Override
  public int hashCode() {
    return lhs.hashCode() ^ rhs.hashCode() + lattr.hashCode() ^ rattr.hashCode();
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) return true;
    if (o instanceof LEquiJoin) {
      return lhs.equals(((LEquiJoin) o).lhs) && rhs.equals(((LEquiJoin) o).rhs)
          && lattr.equals(((LEquiJoin) o).lattr) && rattr.equals(((LEquiJoin) o).rattr);
    }
    return false;
  }

  private static Map<String, Integer> assignAttrIndices(LTerm lhs, LTerm rhs) {
    Map<String, Integer> map = new LinkedHashMap<String, Integer>(lhs.attrIndices);
    int lSize = lhs.attrIndices.size();
    int index = 0;
    for (Map.Entry<String, Integer> entry : rhs.attrIndices.entrySet()) {
      // ensure indices in RHS is ordered
      assert entry.getValue() == index;
      map.put(entry.getKey(), lSize + index);
      ++index;
    }
    return map;
  }

  @Override
  public LTerm substitute(LTerm from, LTerm to) {
    if (this.equals(from)) return to;
    return new LEquiJoin((LTerm) lhs.substitute(from, to), (LTerm) rhs.substitute(from, to), lattr,
        rattr);
  }

}
