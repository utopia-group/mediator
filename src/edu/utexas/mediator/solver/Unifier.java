package edu.utexas.mediator.solver;

import java.util.HashMap;
import java.util.Map;

public class Unifier {

  private static final String Y = "y_";
  private Map<String, String> map;

  public Unifier() {
    map = new HashMap<>();
  }

  public boolean containsKey(String key) {
    return map.containsKey(key);
  }

  public String mapsTo(String key) {
    assert map.containsKey(key);
    return map.get(key);
  }

  public void addMapping(String src, String tgt) {
    if (!map.containsKey(src)) {
      map.put(src, tgt);
    } else if (map.get(src).equals(tgt)) {
      ; // keep it
    } else if (!map.get(src).startsWith(Y) && tgt.startsWith(Y)) {
      ; // keep it
    } else if (map.get(src).startsWith(Y) && !tgt.startsWith(Y)) {
      map.put(src, tgt); // update it
    } else {
      throw new RuntimeException("Unification error: (" + src + "," + tgt + ") in" + map);
    }
  }

  @Override
  public String toString() {
    return map.toString();
  }

  @Override
  public int hashCode() {
    return map.hashCode();
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) return true;
    if (o instanceof Unifier) {
      return map.equals(((Unifier) o).map);
    }
    return false;
  }

}
