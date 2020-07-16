package edu.utexas.mediator.parser.test;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import edu.utexas.mediator.ast.AttrDecl;
import edu.utexas.mediator.ast.Schema;
import edu.utexas.mediator.parser.AntlrParser;

public class SchemaGenTest {

  @Test
  public void testSchema() {
    List<String> relations = new ArrayList<>();
    String relStrA = "A(int a, int aa, int b_fk)";
    String relStrB = "B(int b, String bb)";
    relations.add(relStrA);
    relations.add(relStrB);
    List<String> pks = new ArrayList<>();
    pks.add("A(a)");
    pks.add("B(b)");
    List<String> fks = new ArrayList<>();
    fks.add("A(b_fk) -> B(b)");
    Schema schema = new AntlrParser().parseSchema(relations, pks, fks);
    Assert.assertTrue(schema.getRelDecls().get(0).toString().equals(relStrA));
    Assert.assertTrue(schema.getRelDecls().get(1).toString().equals(relStrB));
    Assert.assertTrue(schema.getRelDecls().get(0).getAttrDeclByName("a").isPrimaryKey());
    Assert.assertTrue(schema.getRelDecls().get(1).getAttrDeclByName("b").isPrimaryKey());
    AttrDecl fkAttr = schema.getRelDeclByName("A").getAttrDeclByName("b_fk");
    Assert.assertTrue(fkAttr.isForeignKey());
    Assert.assertTrue(fkAttr.getReferenceAttrDecl().getRelDecl().getName().equals("B"));
    Assert.assertTrue(fkAttr.getReferenceAttrDecl().getName().equals("b"));
  }

}
