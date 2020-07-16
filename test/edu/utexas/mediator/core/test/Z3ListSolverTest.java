package edu.utexas.mediator.core.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import edu.utexas.mediator.Enumerations.Operator;
import edu.utexas.mediator.ast.AttrDecl;
import edu.utexas.mediator.ast.RelDecl;
import edu.utexas.mediator.constraint.Constraint;
import edu.utexas.mediator.constraint.Context;
import edu.utexas.mediator.constraint.LAttrConst;
import edu.utexas.mediator.constraint.LAttrList;
import edu.utexas.mediator.constraint.LConjunctList;
import edu.utexas.mediator.constraint.LPred;
import edu.utexas.mediator.constraint.LRelVar;
import edu.utexas.mediator.constraint.LTerm;
import edu.utexas.mediator.core.Z3ListSolver;

public class Z3ListSolverTest {

  private Z3ListSolver z3ListSolver = new Z3ListSolver(500); // timeout 500ms
  private Context ctx = new Context();

  private RelDecl A, B, C;
  private LTerm RA, RB, RC;
  private LAttrList L1, L2;
  private LTerm RAa, RBa, RCa;
  private LAttrConst PK, FK;
  private Constraint inv;

  public Z3ListSolverTest() {
    {
      B = new RelDecl();
      B.setName("B");
      List<AttrDecl> attrsB = new ArrayList<>();
      attrsB.add(new AttrDecl("int", "pk"));
      attrsB.add(new AttrDecl("String", "addr1"));
      B.setAttrDecls(attrsB);
      RB = ctx.mkRelConst(B);
      RelDecl Ba = new RelDecl();
      Ba.setName("Ba");
      Ba.setAttrDecls(attrsB);
      RBa = ctx.mkRelConst(Ba);
    }
    {
      A = new RelDecl();
      A.setName("A");
      List<AttrDecl> attrsA = new ArrayList<>();
      attrsA.add(new AttrDecl("int", "id1"));
      attrsA.add(new AttrDecl("String", "name1"));
      AttrDecl fk = new AttrDecl("int", "fk");
      fk.setRelDecl(A);
      fk.setForeignKey(true);
      fk.setReferenceAttrDecl(B.getAttrDeclByName("pk"));
      attrsA.add(fk);
      A.setAttrDecls(attrsA);
      RA = ctx.mkRelConst(A);
      RelDecl Aa = new RelDecl();
      Aa.setName("Aa");
      Aa.setAttrDecls(attrsA);
      RAa = ctx.mkRelConst(Aa);
    }
    {
      C = new RelDecl();
      C.setName("C");
      List<AttrDecl> attrsC = new ArrayList<>();
      attrsC.add(new AttrDecl("int", "id2"));
      attrsC.add(new AttrDecl("String", "name2"));
      attrsC.add(new AttrDecl("String", "addr2"));
      C.setAttrDecls(attrsC);
      RC = ctx.mkRelConst(C);
      RelDecl Ca = new RelDecl();
      Ca.setName("Ca");
      Ca.setAttrDecls(attrsC);
      RCa = ctx.mkRelConst(Ca);
    }
    L1 = ctx.mkAttrList(new ArrayList<>(Arrays.asList(new String[] { "name1", "addr1" })));
    L2 = ctx.mkAttrList(new ArrayList<>(Arrays.asList(new String[] { "name2", "addr2" })));
    PK = ctx.mkAttrConst("pk");
    FK = ctx.mkAttrConst("fk");
    inv = buildInvariant();
  }

  @Test
  public void testTautology1() {
    Constraint andLhs = ctx.mkEqTerm(RA, RB);
    Constraint andRhs = ctx.mkEqTerm(RC, RB);
    Constraint concl = ctx.mkEqTerm(RA, RC);
    Constraint cstr = ctx.mkImply(ctx.mkAnd(andLhs, andRhs), concl);
    boolean valid = z3ListSolver.isValid(cstr);
    Assert.assertTrue(valid);
  }

  @Test
  public void testExists() {
    LRelVar var = ctx.mkRelVar("r1", B.getAttrDecls());
    Constraint premise = ctx.mkExists(var, ctx.mkAnd(ctx.mkEqTerm(var, RA), ctx.mkEqTerm(var, RB)));
    Constraint concl = ctx.mkEqTerm(RA, RB);
    Constraint cstr = ctx.mkImply(premise, concl);
    boolean valid = z3ListSolver.isValid(cstr);
    Assert.assertTrue(valid);
  }

  @Test
  public void testInvInvalid() {
    boolean valid = z3ListSolver.isValid(inv);
    Assert.assertFalse(valid);
  }

  @Test
  public void testQuery() {
    LTerm lhsTerm = ctx.mkSelect(
        ctx.mkPredAttrVal(ctx.mkAttrConst("name1"), Operator.EQ, ctx.mkValConst("name")),
        ctx.mkEquiJoin(RA, RB, FK, PK));
    String[] lhsAttrArray = new String[] { "addr1" };
    LAttrList lhsAttrList = ctx.mkAttrList(new ArrayList<>(Arrays.asList(lhsAttrArray)));
    LTerm lhs = ctx.mkProject(lhsAttrList, lhsTerm);
    LTerm rhsTerm = ctx.mkSelect(
        ctx.mkPredAttrVal(ctx.mkAttrConst("name2"), Operator.EQ, ctx.mkValConst("name")), RC);
    String[] rhsAttrArray = new String[] { "addr2" };
    LAttrList rhsAttrList = ctx.mkAttrList(new ArrayList<>(Arrays.asList(rhsAttrArray)));
    LTerm rhs = ctx.mkProject(rhsAttrList, rhsTerm);
    Constraint concl = ctx.mkEqTerm(lhs, rhs);
    Constraint cstr = ctx.mkImply(inv, concl);
    boolean valid = z3ListSolver.isValid(cstr);
    Assert.assertTrue(valid);
  }

  @Test
  public void testInsertion() {
    Constraint premises = ctx.mkAnd(
        ctx.mkAnd(ctx.mkEqTerm(ctx.mkRelNil(), ctx.mkEquiJoin(RA, RBa, FK, PK)),
            ctx.mkEqTerm(ctx.mkRelNil(), ctx.mkEquiJoin(RAa, RB, FK, PK))),
        ctx.mkEqTerm(ctx.mkProject(L1, ctx.mkEquiJoin(RAa, RBa, FK, PK)), ctx.mkProject(L2, RCa)));
    LTerm lhs = ctx.mkProject(L1,
        ctx.mkEquiJoin(ctx.mkUnion(RA, RAa), ctx.mkUnion(RB, RBa), FK, PK));
    LTerm rhs = ctx.mkProject(L2, ctx.mkUnion(RC, RCa));
    Constraint concl = ctx.mkEqTerm(lhs, rhs);
    Constraint cstr = ctx.mkImply(premises, ctx.mkImply(inv, concl));
    boolean valid = z3ListSolver.isValid(cstr);
    Assert.assertTrue(valid);
  }

  @Test
  public void testDeletion() {
    LPred lhsLhsPred = ctx.mkPredAttrVal(ctx.mkAttrConst("name1"), Operator.EQ,
        ctx.mkValConst("name"));
    LTerm lhsLhs = ctx.mkMinus(RA, ctx.mkSelect(lhsLhsPred, RA));
    LAttrList attrList = ctx.mkAttrList(Arrays.asList(new String[] { "fk" }));
    LPred lhsRhsPred = ctx.mkPredAttrTerm(PK,
        ctx.mkProject(attrList, ctx.mkSelect(lhsLhsPred, RA)));
    LTerm lhsRhs = ctx.mkMinus(RB, ctx.mkSelect(lhsRhsPred, RB));
    LTerm lhs = ctx.mkProject(L1, ctx.mkEquiJoin(lhsLhs, lhsRhs, FK, PK));
    LPred rhsPred = ctx.mkPredAttrVal(ctx.mkAttrConst("name2"), Operator.EQ,
        ctx.mkValConst("name"));
    LTerm rhs = ctx.mkProject(L2, ctx.mkMinus(RC, ctx.mkSelect(rhsPred, RC)));
    Constraint concl = ctx.mkEqTerm(lhs, rhs);
    Constraint cstr = ctx.mkImply(inv, concl);
    boolean valid = z3ListSolver.isValid(cstr);
    Assert.assertTrue(valid);
  }

  @Test
  public void testUpdate() {
    LPred lhsInnerPred = ctx.mkPredAttrVal(ctx.mkAttrConst("name1"), Operator.EQ,
        ctx.mkValConst("name"));
    LAttrList attrList = ctx.mkAttrList(Arrays.asList(new String[] { "fk" }));
    LPred lhsPred = ctx.mkPredAttrTerm(PK, ctx.mkProject(attrList, ctx.mkSelect(lhsInnerPred, RA)));
    LTerm lhsUpd = ctx.mkWrite(ctx.mkSelect(lhsPred, RB), ctx.mkAttrConst("addr1"),
        ctx.mkValConst("addr"));
    LTerm lhs = ctx.mkProject(L1, ctx.mkEquiJoin(RA,
        ctx.mkUnion(ctx.mkMinus(RB, ctx.mkSelect(lhsPred, RB)), lhsUpd), FK, PK));
    LPred rhsPred = ctx.mkPredAttrVal(ctx.mkAttrConst("name2"), Operator.EQ,
        ctx.mkValConst("name"));
    LTerm rhs = ctx.mkProject(L2, ctx.mkUnion(ctx.mkMinus(RC, ctx.mkSelect(rhsPred, RC)),
        ctx.mkWrite(ctx.mkSelect(rhsPred, RC), ctx.mkAttrConst("addr2"), ctx.mkValConst("addr"))));
    Constraint concl = ctx.mkEqTerm(lhs, rhs);
    LConjunctList widthList = ctx.mkConjunctList();
    widthList.addConstraint(ctx.mkWidth("A", 3));
    widthList.addConstraint(ctx.mkWidth("B", 2));
    widthList.addConstraint(ctx.mkWidth("C", 3));
    Constraint cstr = ctx.mkImply(ctx.mkAnd(widthList, inv), concl);
    boolean valid = z3ListSolver.isValid(cstr);
    Assert.assertTrue(valid);
  }

  private Constraint buildInvariant() {
    LTerm lhs = ctx.mkProject(L1, ctx.mkEquiJoin(RA, RB, FK, PK));
    LTerm rhs = ctx.mkProject(L2, RC);
    return ctx.mkEqTerm(lhs, rhs);
  }

}
