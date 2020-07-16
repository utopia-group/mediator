package edu.utexas.mediator.core.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import edu.utexas.mediator.ast.AttrDecl;
import edu.utexas.mediator.core.ArgMapping;
import edu.utexas.mediator.core.PrunedSynthesizer;

public class PrunedSynthTest {

  @Test
  public void testGenAttrDeclList() {
    AttrDecl[] attrs = new AttrDecl[4];
    for (int i = 0; i < attrs.length; ++i) {
      attrs[i] = new AttrDecl("int", "a" + String.valueOf(i));
    }
    String[] strs = { "a", "b" };
    ArgMapping mapping = new ArgMapping();
    for (int i = 0; i < strs.length; ++i) {
      mapping.addMapping(strs[i], attrs[2 * i]);
      mapping.addMapping(strs[i], attrs[2 * i + 1]);
    }
    List<String> args = new ArrayList<>(Arrays.asList(strs));
    List<List<AttrDecl>> list = new PrunedSynthesizer().genAttrDeclList(args, mapping);
    List<List<AttrDecl>> result = new ArrayList<>();
    result.add(new ArrayList<>(Arrays.asList(new AttrDecl[] { attrs[1], attrs[3] })));
    result.add(new ArrayList<>(Arrays.asList(new AttrDecl[] { attrs[1], attrs[2] })));
    result.add(new ArrayList<>(Arrays.asList(new AttrDecl[] { attrs[0], attrs[3] })));
    result.add(new ArrayList<>(Arrays.asList(new AttrDecl[] { attrs[0], attrs[2] })));
    Assert.assertTrue(list.equals(result));
  }

}
