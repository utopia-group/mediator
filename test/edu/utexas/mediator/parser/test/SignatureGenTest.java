package edu.utexas.mediator.parser.test;

import org.junit.Assert;
import org.junit.Test;

import edu.utexas.mediator.ast.Signature;
import edu.utexas.mediator.parser.AntlrParser;

public class SignatureGenTest {

  @Test
  public void testSignature1() {
    String input = "int tran()";
    Signature signature = new AntlrParser().parseSignature(input);
    Assert.assertTrue(input.equals(signature.toString()));
  }

  @Test
  public void testSignature2() {
    String input = "int tran(String a, int b, String c)";
    Signature signature = new AntlrParser().parseSignature(input);
    Assert.assertTrue(input.equals(signature.toString()));
  }

  @Test
  public void testSignature3() {
    String input = "List<String> tran(int a, String b, int c)";
    Signature signature = new AntlrParser().parseSignature(input);
    Assert.assertTrue(input.equals(signature.toString()));
  }

}
