package edu.utexas.mediator.core;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.utexas.mediator.ast.AttrDecl;

public class ArgMapping {

  private Map<String, Set<AttrDecl>> map;

  public ArgMapping() {
    map = new LinkedHashMap<>();
  }

  public void addMapping(String arg, AttrDecl attr) {
    if (!map.containsKey(arg)) {
      map.put(arg, new HashSet<>());
    }
    map.get(arg).add(attr);
  }

  public List<AttrDecl> getMapping(String arg) {
    if (!map.containsKey(arg)) {
      // TODO - remove this assertion
      assert false : "Argument mapping does not contain key: " + arg;
      return new ArrayList<>();
    } else {
      return new ArrayList<>(map.get(arg));
    }
  }

  @Override
  public String toString() {
    return map.toString();
  }

}
