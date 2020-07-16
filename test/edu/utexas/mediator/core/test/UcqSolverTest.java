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
import edu.utexas.mediator.constraint.Invariant;
import edu.utexas.mediator.constraint.LAttrList;
import edu.utexas.mediator.constraint.LEqTerm;
import edu.utexas.mediator.constraint.LPredAttrVal;
import edu.utexas.mediator.constraint.LProject;
import edu.utexas.mediator.core.ISolver;
import edu.utexas.mediator.solver.UcqSolver;

public class UcqSolverTest {

  private Context ctx;
  private RelDecl EMP_s;
  private RelDecl EMP_t;

  public UcqSolverTest() {
    ctx = new Context();
    init();
  }

  private void init() {
    {
      EMP_s = new RelDecl("EMP_s");
      List<AttrDecl> attrDecls = new ArrayList<>();
      AttrDecl eid = new AttrDecl("int", "eid_s");
      AttrDecl ename = new AttrDecl("String", "ename_s");
      eid.setPrimaryKey(true);
      attrDecls.add(eid);
      attrDecls.add(ename);
      EMP_s.setAttrDecls(attrDecls);
    }
    {
      EMP_t = new RelDecl("EMP_t");
      List<AttrDecl> attrDecls = new ArrayList<>();
      AttrDecl eid = new AttrDecl("int", "eid_t");
      AttrDecl ename = new AttrDecl("String", "ename_t");
      AttrDecl esal = new AttrDecl("int", "esal_t");
      eid.setPrimaryKey(true);
      attrDecls.add(eid);
      attrDecls.add(ename);
      attrDecls.add(esal);
      EMP_t.setAttrDecls(attrDecls);
    }
  }

  private Constraint genEmpCstr() {
    Invariant inv = ctx.mkInvariant();
    {
      String[] attrArrayS = { "eid_s", "ename_s" };
      LAttrList attrListS = ctx.mkAttrList(new ArrayList<>(Arrays.asList(attrArrayS)));
      LProject projS = ctx.mkProject(attrListS, ctx.mkRelConst(EMP_s));
      String[] attrArrayT = { "eid_t", "ename_t" };
      LAttrList attrListT = ctx.mkAttrList(new ArrayList<>(Arrays.asList(attrArrayT)));
      LProject projT = ctx.mkProject(attrListT, ctx.mkRelConst(EMP_t));
      inv.addConstraint(ctx.mkEqTerm(projS, projT));
    }
    LEqTerm concl;
    {
      String[] attrArrayS = { "eid_s" };
      LAttrList attrListS = ctx.mkAttrList(new ArrayList<>(Arrays.asList(attrArrayS)));
      LPredAttrVal predS = ctx.mkPredAttrVal(ctx.mkAttrConst("ename_s"), Operator.EQ,
          ctx.mkValConst("name"));
      LProject projS = ctx.mkProject(attrListS, ctx.mkSelect(predS, ctx.mkRelConst(EMP_s)));
      String[] attrArrayT = { "eid_t" };
      LAttrList attrListT = ctx.mkAttrList(new ArrayList<>(Arrays.asList(attrArrayT)));
      LPredAttrVal predT = ctx.mkPredAttrVal(ctx.mkAttrConst("ename_t"), Operator.EQ,
          ctx.mkValConst("name"));
      LProject projT = ctx.mkProject(attrListT, ctx.mkSelect(predT, ctx.mkRelConst(EMP_t)));
      concl = ctx.mkEqTerm(projS, projT);
    }
    return ctx.mkImply(inv, concl);
  }

  private Constraint genEmpCstrWrong() {
    Invariant inv = ctx.mkInvariant();
    {
      String[] attrArrayS = { "eid_s", "ename_s" };
      LAttrList attrListS = ctx.mkAttrList(new ArrayList<>(Arrays.asList(attrArrayS)));
      LProject projS = ctx.mkProject(attrListS, ctx.mkRelConst(EMP_s));
      String[] attrArrayT = { "ename_t", "eid_t" };
      LAttrList attrListT = ctx.mkAttrList(new ArrayList<>(Arrays.asList(attrArrayT)));
      LProject projT = ctx.mkProject(attrListT, ctx.mkRelConst(EMP_t));
      inv.addConstraint(ctx.mkEqTerm(projS, projT));
    }
    LEqTerm concl;
    {
      String[] attrArrayS = { "eid_s" };
      LAttrList attrListS = ctx.mkAttrList(new ArrayList<>(Arrays.asList(attrArrayS)));
      LPredAttrVal predS = ctx.mkPredAttrVal(ctx.mkAttrConst("ename_s"), Operator.EQ,
          ctx.mkValConst("name"));
      LProject projS = ctx.mkProject(attrListS, ctx.mkSelect(predS, ctx.mkRelConst(EMP_s)));
      String[] attrArrayT = { "eid_t" };
      LAttrList attrListT = ctx.mkAttrList(new ArrayList<>(Arrays.asList(attrArrayT)));
      LPredAttrVal predT = ctx.mkPredAttrVal(ctx.mkAttrConst("ename_t"), Operator.EQ,
          ctx.mkValConst("name"));
      LProject projT = ctx.mkProject(attrListT, ctx.mkSelect(predT, ctx.mkRelConst(EMP_t)));
      concl = ctx.mkEqTerm(projS, projT);
    }
    return ctx.mkImply(inv, concl);
  }

  @Test
  public void testEmp() {
    Constraint cstr = genEmpCstr();
    ISolver solver = new UcqSolver();
    Assert.assertTrue(solver.isValid(cstr));
  }

  @Test
  public void testEmpWrong() {
    Constraint cstr = genEmpCstrWrong();
    ISolver solver = new UcqSolver();
    Assert.assertFalse(solver.isValid(cstr));
  }

}
