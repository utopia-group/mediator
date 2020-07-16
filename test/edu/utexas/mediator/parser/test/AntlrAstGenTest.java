package edu.utexas.mediator.parser.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import edu.utexas.mediator.ast.AstNode;
import edu.utexas.mediator.ast.AttrDecl;
import edu.utexas.mediator.ast.RelDecl;
import edu.utexas.mediator.ast.Schema;
import edu.utexas.mediator.parser.AntlrParser;

public class AntlrAstGenTest {

  private AstNode astGen(String input) {
    List<RelDecl> relDeclList = new ArrayList<>();
    RelDecl A = new RelDecl("A");
    RelDecl B = new RelDecl("B");
    RelDecl C = new RelDecl("C");
    RelDecl D = new RelDecl("D");
    AttrDecl c = new AttrDecl("int", "c");
    c.setRelDecl(C);
    AttrDecl d = new AttrDecl("int", "d");
    d.setRelDecl(D);
    d.setForeignKey(true);
    d.setReferenceAttrDecl(c);
    C.setAttrDecls(new ArrayList<>(Arrays.asList(new AttrDecl[] { c })));
    D.setAttrDecls(new ArrayList<>(Arrays.asList(new AttrDecl[] { d })));

    RelDecl Member = new RelDecl("Member");
    RelDecl Address = new RelDecl("Address");
    AttrDecl aid = new AttrDecl("int", "aid");
    aid.setRelDecl(Address);
    AttrDecl afk = new AttrDecl("int", "afk");
    afk.setRelDecl(Member);
    afk.setForeignKey(true);
    afk.setReferenceAttrDecl(aid);
    Address.setAttrDecls(new ArrayList<>(Arrays.asList(new AttrDecl[] { aid })));
    Member.setAttrDecls(new ArrayList<>(Arrays.asList(new AttrDecl[] { afk })));

    relDeclList.addAll(Arrays.asList(new RelDecl[] { A, B, C, D, Member, Address }));
    Schema schema = new Schema(relDeclList);
    AntlrParser parser = new AntlrParser();
    return parser.parseStatement(input, schema);
  }

  private boolean equalsWithWhiteSpace(String src, String tgt) {
    return src.replaceAll("\\s+", "").equals(tgt.replaceAll("\\s+", ""));
  }

  @Test
  public void testBinaryRelOperator() {
    String input = "minus(A, cup(B, join(C, D)))";
    AstNode ast = astGen(input);
    Assert.assertTrue(equalsWithWhiteSpace(input, ast.toString()));
  }

  @Test
  public void testProjectNode() {
    String input = "pi([a, b, c, d], A)";
    AstNode ast = astGen(input);
    Assert.assertTrue(equalsWithWhiteSpace(input, ast.toString()));
  }

  @Test
  public void testSelectNode() {
    String input = "sigma(a=b, A)";
    AstNode ast = astGen(input);
    Assert.assertTrue(equalsWithWhiteSpace(input, ast.toString()));
  }

  @Test
  public void testInPredNode() {
    String input = "sigma(in(aid, pi([afk], B)), A)";
    AstNode ast = astGen(input);
    Assert.assertTrue(equalsWithWhiteSpace(input, ast.toString()));
  }

  @Test
  public void testAndPredNode() {
    String input = "sigma(and(a=b, c=d), A)";
    AstNode ast = astGen(input);
    Assert.assertTrue(equalsWithWhiteSpace(input, ast.toString()));
  }

  @Test
  public void testOrPredNode() {
    String input = "sigma(or(a=b, c=d), A)";
    AstNode ast = astGen(input);
    Assert.assertTrue(equalsWithWhiteSpace(input, ast.toString()));
  }

  @Test
  public void testNotPredNode() {
    String input = "sigma(not(a=b), A)";
    AstNode ast = astGen(input);
    Assert.assertTrue(equalsWithWhiteSpace(input, ast.toString()));
  }

  @Test
  public void testInsertNode() {
    String input = "ins(A, (a, aa, aaa))";
    AstNode ast = astGen(input);
    Assert.assertTrue(equalsWithWhiteSpace(input, ast.toString()));
  }

  @Test
  public void testDeleteNode() {
    String input = "del(A, a=b)";
    AstNode ast = astGen(input);
    Assert.assertTrue(equalsWithWhiteSpace(input, ast.toString()));
  }

  @Test
  public void testUpdateNode() {
    String input = "upd(A, a=b, c, d)";
    AstNode ast = astGen(input);
    Assert.assertTrue(equalsWithWhiteSpace(input, ast.toString()));
  }

  @Test
  public void testCombination1() {
    String input = "del(Address, in(aid, pi([afk], sigma(manme=name, Member))))";
    AstNode ast = astGen(input);
    Assert.assertTrue(equalsWithWhiteSpace(input, ast.toString()));
  }

  @Test
  public void testCombination2() {
    String input = "upd(Address, in(aid, pi([afk], sigma(manme=name, Member))), maddr, addr)";
    AstNode ast = astGen(input);
    Assert.assertTrue(equalsWithWhiteSpace(input, ast.toString()));
  }

  @Test
  public void testCombination3() {
    String input = "pi([maddr], sigma(mname=name, join(Member, Address)))";
    AstNode ast = astGen(input);
    Assert.assertTrue(equalsWithWhiteSpace(input, ast.toString()));
  }

}
