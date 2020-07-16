package edu.utexas.mediator.util;

import java.util.LinkedHashMap;
import java.util.Map;

import edu.utexas.mediator.ast.AttrDecl;
import edu.utexas.mediator.ast.Program;
import edu.utexas.mediator.ast.RelDecl;

public class StatUtil {

  public static Map<String, Integer> getStatistics(Program prog) {
    int relCount = prog.getSchema().getRelDecls().size();
    int attrCount = 0;
    int pkCount = 0;
    int fkCount = 0;
    for (RelDecl relDecl : prog.getSchema().getRelDecls()) {
      attrCount += relDecl.getAttrNum();
      for (AttrDecl attrDecl : relDecl.getAttrDecls()) {
        if (attrDecl.isPrimaryKey()) ++pkCount;
        if (attrDecl.isForeignKey()) ++fkCount;
      }
    }
    int tranCount = prog.getTransactions().size();
    Map<String, Integer> map = new LinkedHashMap<>();
    map.put("Trans", tranCount);
    map.put("Rels", relCount);
    map.put("Attrs", attrCount);
    map.put("Pks", pkCount);
    map.put("Fks", fkCount);
    return map;
  }

}
