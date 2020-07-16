package edu.utexas.mediator.constraint.test;

import org.junit.Assert;
import org.junit.Test;

import edu.utexas.mediator.ast.RelDecl;
import edu.utexas.mediator.constraint.Constraint;
import edu.utexas.mediator.constraint.Context;
import edu.utexas.mediator.constraint.LRelConst;
import edu.utexas.mediator.constraint.LRelVar;

public class SubstitutionTest {

  private Context ctx;

  public SubstitutionTest() {
    ctx = new Context();
  }

  @Test
  public void testSubstitution1() {
    RelDecl r1 = new RelDecl("r1");
    RelDecl r2 = new RelDecl("r2");
    LRelConst R1 = ctx.mkRelConst(r1);
    LRelConst R2 = ctx.mkRelConst(r2);
    LRelVar x = ctx.mkRelVar("x", r1.getAttrDecls());
    Constraint eq = ctx.mkEqTerm(R1, R2);
    Constraint cstr = ctx.mkExists(x, eq.substitute(R2, x));
    Constraint result = ctx.mkExists(x, ctx.mkEqTerm(R1, x));
    Assert.assertTrue(cstr.equals(result));
  }

  @Test
  public void testSubstitution2() {
    RelDecl r1 = new RelDecl("r1");
    RelDecl r2 = new RelDecl("r2");
    RelDecl r3 = new RelDecl("r3");
    LRelConst R1 = ctx.mkRelConst(r1);
    LRelConst R2 = ctx.mkRelConst(r2);
    LRelConst R3 = ctx.mkRelConst(r3);
    LRelVar x = ctx.mkRelVar("x", r1.getAttrDecls());
    Constraint eq = ctx.mkEqTerm(R1, ctx.mkUnion(R2, R3));
    Constraint cstr = ctx.mkExists(x, eq.substitute(ctx.mkUnion(R2, R3), ctx.mkUnion(R3, R2)));
    Constraint result = ctx.mkExists(x, ctx.mkEqTerm(R1, ctx.mkUnion(R3, R2)));
    Assert.assertTrue(cstr.equals(result));
  }

}
