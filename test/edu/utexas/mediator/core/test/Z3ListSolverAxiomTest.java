package edu.utexas.mediator.core.test;

import org.junit.Assert;
import org.junit.Test;

import com.microsoft.z3.Context;
import com.microsoft.z3.Expr;
import com.microsoft.z3.FuncDecl;
import com.microsoft.z3.ListSort;
import com.microsoft.z3.Params;
import com.microsoft.z3.Solver;
import com.microsoft.z3.Sort;
import com.microsoft.z3.Status;

import edu.utexas.mediator.core.Z3ListSolver;

public class Z3ListSolverAxiomTest {

  private Z3ListSolver z3ListSolver = new Z3ListSolver();
  private Context z3Ctx = z3ListSolver.getZ3Context();
  private ListSort attrListSort = (ListSort) z3ListSolver.getSortByName("AttrList");
  private Sort cellSort = z3ListSolver.getSortByName("Cell");
  private ListSort tupleSort = (ListSort) z3ListSolver.getSortByName("Tuple");
  private ListSort relSort = (ListSort) z3ListSolver.getSortByName("Relation");
  private Expr[] t = new Expr[7];
  private Expr[] r = new Expr[7];
  private Expr[][] c = new Expr[4][5];

  public Z3ListSolverAxiomTest() {
    for (int i = 0; i < 7; ++i) {
      t[i] = z3Ctx.mkConst("t" + String.valueOf(i), tupleSort);
      r[i] = z3Ctx.mkConst("r" + String.valueOf(i), relSort);
    }
    for (int i = 0; i < 4; ++i) {
      for (int j = 0; j < 5; ++j) {
        c[i][j] = z3Ctx.mkConst("v" + String.valueOf(i) + String.valueOf(j), cellSort);
      }
    }
  }

  @Test
  public void testAxiomAppend() {
    Solver solver = getFreshAxiomatizedSolver();
    FuncDecl funcAppend = z3ListSolver.getFunctionByName("append");
    solver.add(z3Ctx.mkEq(r[1], buildList(new Expr[] { t[1], t[2] }, relSort)));
    solver.add(z3Ctx.mkEq(r[2], buildList(new Expr[] { t[3], t[4] }, relSort)));
    solver.add(z3Ctx.mkEq(r[3], buildList(new Expr[] { t[1], t[2], t[3], t[4] }, relSort)));
    solver.add(z3Ctx.mkNot(z3Ctx.mkEq(funcAppend.apply(r[1], r[2]), r[3])));
    Status status = solver.check();
    Assert.assertTrue(status == Status.UNSATISFIABLE);
  }

  @Test
  public void testAxiomProd() {
    Solver solver = getFreshAxiomatizedSolver();
    FuncDecl funcProd = z3ListSolver.getFunctionByName("prod");
    solver.add(z3Ctx.mkEq(t[1], buildList(new Expr[] { c[1][1], c[1][2], c[1][3] }, tupleSort)));
    solver.add(z3Ctx.mkEq(t[2], buildList(new Expr[] { c[2][1], c[2][2], c[2][3] }, tupleSort)));
    solver.add(z3Ctx.mkEq(t[3], buildList(new Expr[] { c[1][4] }, tupleSort)));
    solver.add(z3Ctx.mkEq(r[1], buildList(new Expr[] { t[1], t[2] }, relSort)));
    solver.add(z3Ctx.mkEq(r[2], buildList(new Expr[] { t[3] }, relSort)));
    solver.add(
        z3Ctx.mkEq(t[4], buildList(new Expr[] { c[1][1], c[1][2], c[1][3], c[1][4] }, tupleSort)));
    solver.add(
        z3Ctx.mkEq(t[5], buildList(new Expr[] { c[2][1], c[2][2], c[2][3], c[1][4] }, tupleSort)));
    solver.add(z3Ctx.mkEq(r[3], buildList(new Expr[] { t[4], t[5] }, relSort)));
    solver.add(z3Ctx.mkNot(z3Ctx.mkEq(funcProd.apply(r[1], r[2]), r[3])));
    Status status = solver.check();
    Assert.assertTrue(status == Status.UNSATISFIABLE);
  }

  @Test
  public void testAxiomProj() {
    Solver solver = getFreshAxiomatizedSolver();
    FuncDecl funcProj = z3ListSolver.getFunctionByName("proj");
    Expr l = z3Ctx.mkConst("l", attrListSort);
    solver.add(z3Ctx.mkEq(t[1], buildList(new Expr[] { c[1][1], c[1][2], c[1][3] }, tupleSort)));
    solver.add(z3Ctx.mkEq(t[2], buildList(new Expr[] { c[2][1], c[2][2], c[2][3] }, tupleSort)));
    solver.add(z3Ctx.mkEq(r[1], buildList(new Expr[] { t[1], t[2] }, relSort)));
    solver
        .add(z3Ctx.mkEq(l, buildList(new Expr[] { z3Ctx.mkInt(0), z3Ctx.mkInt(2) }, attrListSort)));
    solver.add(z3Ctx.mkEq(t[3], buildList(new Expr[] { c[1][1], c[1][3] }, tupleSort)));
    solver.add(z3Ctx.mkEq(t[4], buildList(new Expr[] { c[2][1], c[2][3] }, tupleSort)));
    solver.add(z3Ctx.mkEq(r[2], buildList(new Expr[] { t[3], t[4] }, relSort)));
    solver.add(z3Ctx.mkNot(z3Ctx.mkEq(funcProj.apply(l, r[1]), r[2])));
    Status status = solver.check();
    Assert.assertTrue(status == Status.UNSATISFIABLE);
  }

  @Test
  public void testAxiomSelEq() {
    Solver solver = getFreshAxiomatizedSolver();
    FuncDecl funcSelEq = z3ListSolver.getFunctionByName("sel-eq");
    solver.add(z3Ctx.mkNot(z3Ctx.mkEq(c[1][2], c[1][3])));
    solver.add(z3Ctx.mkEq(c[2][2], c[2][3]));
    solver.add(z3Ctx.mkEq(t[1], buildList(new Expr[] { c[1][1], c[1][2], c[1][3] }, tupleSort)));
    solver.add(z3Ctx.mkEq(t[2], buildList(new Expr[] { c[2][1], c[2][2], c[2][3] }, tupleSort)));
    solver.add(z3Ctx.mkEq(r[1], buildList(new Expr[] { t[1], t[2] }, relSort)));
    solver.add(z3Ctx.mkEq(r[2], buildList(new Expr[] { t[2] }, relSort)));
    solver
        .add(z3Ctx.mkNot(z3Ctx.mkEq(funcSelEq.apply(z3Ctx.mkInt(1), z3Ctx.mkInt(2), r[1]), r[2])));
    Status status = solver.check();
    Assert.assertTrue(status == Status.UNSATISFIABLE);
  }

  @Test
  public void testAxiomSelEqv() {
    Solver solver = getFreshAxiomatizedSolver();
    FuncDecl funcSelEqv = z3ListSolver.getFunctionByName("sel-eqv");
    solver.add(z3Ctx.mkNot(z3Ctx.mkEq(c[1][3], c[2][3])));
    solver.add(z3Ctx.mkEq(t[1], buildList(new Expr[] { c[1][1], c[1][2], c[1][3] }, tupleSort)));
    solver.add(z3Ctx.mkEq(t[2], buildList(new Expr[] { c[2][1], c[2][2], c[2][3] }, tupleSort)));
    solver.add(z3Ctx.mkEq(r[1], buildList(new Expr[] { t[1], t[2] }, relSort)));
    solver.add(z3Ctx.mkEq(r[2], buildList(new Expr[] { t[1] }, relSort)));
    solver.add(z3Ctx.mkNot(z3Ctx.mkEq(funcSelEqv.apply(z3Ctx.mkInt(2), c[1][3], r[1]), r[2])));
    Status status = solver.check();
    Assert.assertTrue(status == Status.UNSATISFIABLE);
  }

  @Test
  public void testAxiomEquiJoin() {
    Solver solver = getFreshAxiomatizedSolver();
    FuncDecl funcEquiJoin = z3ListSolver.getFunctionByName("equi-join");
    solver.add(z3Ctx.mkEq(c[1][4], c[1][3]));
    solver.add(z3Ctx.mkEq(t[1], buildList(new Expr[] { c[1][1], c[1][2], c[1][3] }, tupleSort)));
    solver.add(z3Ctx.mkEq(t[2], buildList(new Expr[] { c[2][1], c[2][2], c[2][3] }, tupleSort)));
    solver.add(z3Ctx.mkEq(t[3], buildList(new Expr[] { c[1][4] }, tupleSort)));
    solver.add(z3Ctx.mkEq(r[1], buildList(new Expr[] { t[1], t[2] }, relSort)));
    solver.add(z3Ctx.mkEq(r[2], buildList(new Expr[] { t[3] }, relSort)));
    solver.add(z3Ctx.mkNot(z3Ctx.mkEq(c[2][3], c[1][3])));
    solver.add(
        z3Ctx.mkEq(t[4], buildList(new Expr[] { c[1][1], c[1][2], c[1][3], c[1][4] }, tupleSort)));
    solver.add(z3Ctx.mkEq(r[3], buildList(new Expr[] { t[4] }, relSort)));
    solver.add(z3Ctx
        .mkNot(z3Ctx.mkEq(funcEquiJoin.apply(z3Ctx.mkInt(2), z3Ctx.mkInt(3), r[1], r[2]), r[3])));
    Status status = solver.check();
    Assert.assertTrue(status == Status.UNSATISFIABLE);
  }

  @Test
  public void testAxiomMinus() {
    Solver solver = getFreshAxiomatizedSolver();
    FuncDecl funcMinus = z3ListSolver.getFunctionByName("minus");
    solver.add(z3Ctx.mkEq(r[1], buildList(new Expr[] { t[1], t[2], t[3] }, relSort)));
    solver.add(z3Ctx.mkEq(r[2], buildList(new Expr[] { t[2] }, relSort)));
    solver.add(z3Ctx.mkEq(r[3], buildList(new Expr[] { t[1], t[3] }, relSort)));
    solver.add(z3Ctx.mkNot(z3Ctx.mkEq(t[1], t[2])));
    solver.add(z3Ctx.mkNot(z3Ctx.mkEq(t[1], t[3])));
    solver.add(z3Ctx.mkNot(z3Ctx.mkEq(t[2], t[3])));
    solver.add(z3Ctx.mkNot(z3Ctx.mkEq(funcMinus.apply(r[1], r[2]), r[3])));
    Status status = solver.check();
    Assert.assertTrue(status == Status.UNSATISFIABLE);
  }

  @Test
  public void testAxiomUpd() {
    Solver solver = getFreshAxiomatizedSolver();
    FuncDecl funcUpd = z3ListSolver.getFunctionByName("upd");
    solver.add(z3Ctx.mkEq(t[1], buildList(new Expr[] { c[1][1], c[1][2], c[1][3] }, tupleSort)));
    solver.add(z3Ctx.mkEq(t[2], buildList(new Expr[] { c[2][1], c[2][2], c[2][3] }, tupleSort)));
    solver.add(z3Ctx.mkEq(r[1], buildList(new Expr[] { t[1], t[2] }, relSort)));
    solver.add(z3Ctx.mkEq(t[3], buildList(new Expr[] { c[1][1], c[1][4], c[1][3] }, tupleSort)));
    solver.add(z3Ctx.mkEq(t[4], buildList(new Expr[] { c[2][1], c[1][4], c[2][3] }, tupleSort)));
    solver.add(z3Ctx.mkEq(r[2], buildList(new Expr[] { t[3], t[4] }, relSort)));
    solver.add(z3Ctx.mkNot(z3Ctx.mkEq(funcUpd.apply(r[1], z3Ctx.mkInt(1), c[1][4]), r[2])));
    Status status = solver.check();
    Assert.assertTrue(status == Status.UNSATISFIABLE);
  }

  private Solver getFreshAxiomatizedSolver() {
    Solver solver = z3Ctx.mkSolver();
    Params params = z3Ctx.mkParams();
    params.add("timeout", 1000);
    solver.setParameters(params);
    z3ListSolver.loadAllFuncDecls(solver);
    z3ListSolver.loadAllAxioms(solver);
    return solver;
  }

  private Expr buildList(Expr[] elems, ListSort sort) {
    assert elems.length > 0;
    Expr expr = sort.getNil();
    for (int i = elems.length - 1; i >= 0; --i) {
      expr = sort.getConsDecl().apply(elems[i], expr);
    }
    return expr;
  }

}
