package edu.utexas.mediator.core;

import java.util.HashMap;
import java.util.Map;

import com.microsoft.z3.ArithExpr;
import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Expr;
import com.microsoft.z3.FuncDecl;
import com.microsoft.z3.ListSort;
import com.microsoft.z3.Params;
import com.microsoft.z3.Pattern;
import com.microsoft.z3.Solver;
import com.microsoft.z3.Sort;
import com.microsoft.z3.Status;

import edu.utexas.mediator.Options;
import edu.utexas.mediator.constraint.Constraint;

public class Z3ListSolver implements ISolver {

  private final com.microsoft.z3.Context z3Ctx;
  private final int z3TimeoutMs;
  private final Map<String, Sort> sorts;
  private final Map<String, FuncDecl> functions;

  private final Sort valSort;
  private final Sort attrSort;
  private final ListSort attrListSort;
  private final Sort cellSort;
  private final ListSort tupleSort;
  private final ListSort relSort;
  private final Sort boolSort;

  public Z3ListSolver() {
    this(Options.SOLVER_TIMEOUT);
  }

  public Z3ListSolver(int timeout) {
    z3Ctx = new com.microsoft.z3.Context();
    z3TimeoutMs = timeout;
    sorts = new HashMap<>();
    functions = new HashMap<>();

    // Declare all sorts
    valSort = z3Ctx.mkUninterpretedSort("Val");
    attrSort = z3Ctx.mkIntSort();
    attrListSort = z3Ctx.mkListSort("AttrList", attrSort);
    cellSort = valSort;
    tupleSort = z3Ctx.mkListSort("Tuple", cellSort);
    relSort = z3Ctx.mkListSort("Relation", tupleSort);
    boolSort = z3Ctx.mkBoolSort();
    sorts.put("Val", valSort);
    sorts.put("Attr", attrSort);
    sorts.put("AttrList", attrListSort);
    sorts.put("Cell", cellSort);
    sorts.put("Tuple", tupleSort);
    sorts.put("Relation", relSort);
    sorts.put("Bool", boolSort);
  }

  @Override
  public boolean isValid(Constraint formula) {
    // Initialize the solver
    Solver z3Solver = z3Ctx.mkSolver();
    Params params = z3Ctx.mkParams();
    params.add("smt.auto-config", false);
    params.add("smt.mbqi", false);
    params.add("timeout", z3TimeoutMs);
    params.add("smt.qi.eager_threshold", Options.SOLVER_QI_EAGER_THRES);
    z3Solver.setParameters(params);

    // Load all axioms and theorems
    loadAllFuncDecls(z3Solver);
    loadAllAxioms(z3Solver);
    loadAllTheorems(z3Solver);
    loadAllSymbolicTheorems(z3Solver);

    // Check validity
    if (Options.PRINT_INTERNAL_FORMULA) System.out.println(formula);
    BoolExpr expr = constraintToZ3(formula);
    if (Options.PRINT_Z3_FORMULA) System.out.println(expr);
    z3Solver.add(z3Ctx.mkNot(expr));
    Status status = z3Solver.check();
    if (Options.PRINT_Z3_FORMULA) System.out.println(status);
    return (status == Status.UNSATISFIABLE);
  }

  public BoolExpr constraintToZ3(Constraint cstr) {
    Z3CstrConverter converter = new Z3CstrConverter(this);
    return converter.transform(cstr);
  }

  public void loadAllFuncDecls(Solver solver) {
    loadFuncDecl("append", new Sort[] { relSort, relSort }, relSort);
    loadFuncDecl("cat-tuple", new Sort[] { tupleSort, tupleSort }, tupleSort);
    loadFuncDecl("prod-aux", new Sort[] { tupleSort, relSort }, relSort);
    loadFuncDecl("prod", new Sort[] { relSort, relSort }, relSort);
    loadFuncDecl("get-cell", new Sort[] { attrSort, tupleSort }, cellSort);
    loadFuncDecl("proj-tuple", new Sort[] { attrListSort, tupleSort }, tupleSort);
    loadFuncDecl("proj", new Sort[] { attrListSort, relSort }, relSort);
    loadFuncDecl("sel-eq", new Sort[] { attrSort, attrSort, relSort }, relSort);
    loadFuncDecl("sel-eqv", new Sort[] { attrSort, valSort, relSort }, relSort);
    loadFuncDecl("equi-join", new Sort[] { attrSort, attrSort, relSort, relSort }, relSort);
    loadFuncDecl("minus-one", new Sort[] { relSort, tupleSort }, relSort);
    loadFuncDecl("minus", new Sort[] { relSort, relSort }, relSort);
    loadFuncDecl("upd-tuple", new Sort[] { tupleSort, attrSort, valSort }, tupleSort);
    loadFuncDecl("upd", new Sort[] { relSort, attrSort, valSort }, relSort);
    loadFuncDecl("sub-set", new Sort[] { relSort, relSort }, boolSort);
    loadFuncDecl("sel-in-eqv", new Sort[] { attrSort, valSort, relSort, relSort }, relSort);
    loadFuncDecl("list-pair", new Sort[] { attrListSort, attrListSort, attrListSort, attrListSort },
        boolSort);
    loadFuncDecl("same-index", new Sort[] { attrSort, attrSort, attrListSort, attrListSort },
        boolSort);
    loadFuncDecl("mem-list", new Sort[] { attrSort, attrListSort }, boolSort);
    loadFuncDecl("width", new Sort[] { relSort }, attrSort);
    loadFuncDecl("width-plus", new Sort[] { attrSort, attrSort }, attrSort);
  }

  public void loadAllAxioms(Solver solver) {
    loadAxiomAppend(solver);
    loadAxiomCatTuple(solver);
    loadAxiomProdAux(solver);
    loadAxiomProd(solver);
    loadAxiomGetCell(solver);
    loadAxiomProjTuple(solver);
    loadAxiomProj(solver);
    loadAxiomSelEq(solver);
    loadAxiomSelEqv(solver);
    loadAxiomEquiJoin(solver);
    loadAxiomMinusOne(solver);
    loadAxiomMinus(solver);
    loadAxiomUpdTuple(solver);
    loadAxiomUpd(solver);
    loadAxiomMemList(solver);
  }

  public void loadAllTheorems(Solver solver) {
    loadTheoremAppNil(solver);
    loadTheoremJoinNil(solver);
    loadTheoremMinusSame(solver);
    loadTheoremSelSubSet(solver);
    loadTheoremProjSubSet(solver);
    loadTheoremAppMinusSimpl(solver);
    loadTheoremSelInSimpl(solver);
    loadTheoremSelIdem(solver);
    loadTheoremJoinAppDist(solver);
    loadTheoremProjAppDist(solver);
    loadTheoremJoinMinusDist(solver);
    loadTheoremProjMinusDist(solver);
    loadTheoremProjSubseq(solver);
    loadTheoremSelJoinAssoc(solver);
    loadTheoremSameIndexGen(solver);
    loadTheoremProjUpdElim(solver);
    // loadTheoremUpdRewrite(solver);
    loadTheoremUpdJoinComm(solver);
    if (Options.ENABLE_SEL_DIST) {
      loadTheoremSelAppDist(solver);
      loadTheoremSelMinusDist(solver);
    }
    loadTheoremSelUpdSimpl(solver);
    loadTheoremUpdComm(solver);
    loadTheoremWidthPlus(solver);
    loadTheoremSelJoinPkfk(solver);
  }

  public void loadAllSymbolicTheorems(Solver solver) {
    loadSymbolicTheoremSameIndex(solver);
  }

  public com.microsoft.z3.Context getZ3Context() {
    return z3Ctx;
  }

  public Sort getSortByName(String name) {
    assert sorts.containsKey(name) : "Unknown sort: " + name;
    return sorts.get(name);
  }

  public FuncDecl getFunctionByName(String name) {
    assert functions.containsKey(name) : "Unknown function: " + name;
    return functions.get(name);
  }

  private void loadFuncDecl(String name, Sort[] domainSorts, Sort rangeSort) {
    FuncDecl funcDecl = z3Ctx.mkFuncDecl(name, domainSorts, rangeSort);
    assert !functions.containsKey(name);
    functions.put(name, funcDecl);
  }

  private void loadAxiomAppend(Solver solver) {
    FuncDecl funcAppend = getFunctionByName("append");
    // app_nil
    // append([], r2) = r2
    {
      Expr[] bound = new Expr[1];
      bound[0] = z3Ctx.mkConst("r2", relSort);
      Expr eqLhs = funcAppend.apply(relSort.getNil(), bound[0]);
      BoolExpr body = z3Ctx.mkEq(eqLhs, bound[0]);
      Pattern[] patterns = new Pattern[] { z3Ctx.mkPattern(eqLhs) };
      BoolExpr axiom = z3Ctx.mkForall(bound, body, 1, patterns, null, null, null);
      solver.add(axiom);
    }
    // app_ind
    // append(t :: r, r2) = t :: append(r, r2)
    {
      Expr[] bound = new Expr[3];
      bound[0] = z3Ctx.mkConst("t", tupleSort);
      bound[1] = z3Ctx.mkConst("r", relSort);
      bound[2] = z3Ctx.mkConst("r2", relSort);
      Expr eqLhs = funcAppend.apply(relSort.getConsDecl().apply(bound[0], bound[1]), bound[2]);
      Expr eqRhs = relSort.getConsDecl().apply(bound[0], funcAppend.apply(bound[1], bound[2]));
      BoolExpr body = z3Ctx.mkEq(eqLhs, eqRhs);
      Pattern[] patterns = new Pattern[] { z3Ctx.mkPattern(eqLhs) };
      BoolExpr axiom = z3Ctx.mkForall(bound, body, 1, patterns, null, null, null);
      solver.add(axiom);
    }
  }

  private void loadAxiomCatTuple(Solver solver) {
    FuncDecl funcCatTuple = getFunctionByName("cat-tuple");
    // cat-tuple_nil
    // cat-tuple([], t2) = t2
    {
      Expr[] bound = new Expr[1];
      bound[0] = z3Ctx.mkConst("t2", tupleSort);
      Expr eqLhs = funcCatTuple.apply(tupleSort.getNil(), bound[0]);
      BoolExpr body = z3Ctx.mkEq(eqLhs, bound[0]);
      Pattern[] patterns = new Pattern[] { z3Ctx.mkPattern(eqLhs) };
      BoolExpr axiom = z3Ctx.mkForall(bound, body, 1, patterns, null, null, null);
      solver.add(axiom);
    }
    // cat-tuple_ind
    // cat-tuple(c :: t, t2) = c :: cat-tuple(t, t2)
    {
      Expr[] bound = new Expr[3];
      bound[0] = z3Ctx.mkConst("c", cellSort);
      bound[1] = z3Ctx.mkConst("t", tupleSort);
      bound[2] = z3Ctx.mkConst("t2", tupleSort);
      Expr eqLhs = funcCatTuple.apply(tupleSort.getConsDecl().apply(bound[0], bound[1]), bound[2]);
      Expr eqRhs = tupleSort.getConsDecl().apply(bound[0], funcCatTuple.apply(bound[1], bound[2]));
      BoolExpr body = z3Ctx.mkEq(eqLhs, eqRhs);
      Pattern[] patterns = new Pattern[] { z3Ctx.mkPattern(eqLhs) };
      BoolExpr axiom = z3Ctx.mkForall(bound, body, 1, patterns, null, null, null);
      solver.add(axiom);
    }
  }

  private void loadAxiomProdAux(Solver solver) {
    FuncDecl funcCatTuple = getFunctionByName("cat-tuple");
    FuncDecl funcProdAux = getFunctionByName("prod-aux");
    // prod-aux_nil
    // prod-aux(t, []) = []
    {
      Expr[] bound = new Expr[1];
      bound[0] = z3Ctx.mkConst("t", tupleSort);
      Expr eqLhs = funcProdAux.apply(bound[0], relSort.getNil());
      BoolExpr body = z3Ctx.mkEq(eqLhs, relSort.getNil());
      Pattern[] patterns = new Pattern[] { z3Ctx.mkPattern(eqLhs) };
      BoolExpr axiom = z3Ctx.mkForall(bound, body, 1, patterns, null, null, null);
      solver.add(axiom);
    }
    // prod-aux_ind
    // prod-aux(t, h :: r) = cat-tuple(t, h) :: prod-aux(t, r)
    {
      Expr[] bound = new Expr[3];
      bound[0] = z3Ctx.mkConst("t", tupleSort);
      bound[1] = z3Ctx.mkConst("h", tupleSort);
      bound[2] = z3Ctx.mkConst("r", relSort);
      Expr eqLhs = funcProdAux.apply(bound[0], relSort.getConsDecl().apply(bound[1], bound[2]));
      Expr eqRhs = relSort.getConsDecl().apply(funcCatTuple.apply(bound[0], bound[1]),
          funcProdAux.apply(bound[0], bound[2]));
      BoolExpr body = z3Ctx.mkEq(eqLhs, eqRhs);
      Pattern[] patterns = new Pattern[] { z3Ctx.mkPattern(eqLhs) };
      BoolExpr axiom = z3Ctx.mkForall(bound, body, 1, patterns, null, null, null);
      solver.add(axiom);
    }
  }

  private void loadAxiomProd(Solver solver) {
    FuncDecl funcProdAux = getFunctionByName("prod-aux");
    FuncDecl funcAppend = getFunctionByName("append");
    FuncDecl funcProd = getFunctionByName("prod");
    // prod_nil
    // prod([], r2) = []
    {
      Expr[] bound = new Expr[1];
      bound[0] = z3Ctx.mkConst("r2", relSort);
      Expr eqLhs = funcProd.apply(relSort.getNil(), bound[0]);
      BoolExpr body = z3Ctx.mkEq(eqLhs, relSort.getNil());
      Pattern[] patterns = new Pattern[] { z3Ctx.mkPattern(eqLhs) };
      BoolExpr axiom = z3Ctx.mkForall(bound, body, 1, patterns, null, null, null);
      solver.add(axiom);
    }
    // prod_ind
    // prod(t :: r, r2) = prod-aux(t, r2) ++ prod(r, r2)
    {
      Expr[] bound = new Expr[3];
      bound[0] = z3Ctx.mkConst("t", tupleSort);
      bound[1] = z3Ctx.mkConst("r", relSort);
      bound[2] = z3Ctx.mkConst("r2", relSort);
      Expr eqLhs = funcProd.apply(relSort.getConsDecl().apply(bound[0], bound[1]), bound[2]);
      Expr eqRhs = funcAppend.apply(funcProdAux.apply(bound[0], bound[2]),
          funcProd.apply(bound[1], bound[2]));
      BoolExpr body = z3Ctx.mkEq(eqLhs, eqRhs);
      Pattern[] patterns = new Pattern[] { z3Ctx.mkPattern(eqLhs) };
      BoolExpr axiom = z3Ctx.mkForall(bound, body, 1, patterns, null, null, null);
      solver.add(axiom);
    }
  }

  private void loadAxiomGetCell(Solver solver) {
    FuncDecl funcGetCell = getFunctionByName("get-cell");
    // get-cell_def
    // get-cell(a, c :: t) = (a == 0) ? c : get-cell(a-1, t)
    {
      Expr[] bound = new Expr[3];
      bound[0] = z3Ctx.mkConst("a", attrSort);
      bound[1] = z3Ctx.mkConst("c", cellSort);
      bound[2] = z3Ctx.mkConst("t", tupleSort);
      Expr eqLhs = funcGetCell.apply(bound[0], tupleSort.getConsDecl().apply(bound[1], bound[2]));
      Expr eqRhs = z3Ctx.mkITE(z3Ctx.mkEq(bound[0], z3Ctx.mkInt(0)), bound[1],
          funcGetCell.apply(z3Ctx.mkSub((ArithExpr) bound[0], z3Ctx.mkInt(1)), bound[2]));
      BoolExpr body = z3Ctx.mkEq(eqLhs, eqRhs);
      Pattern[] patterns = new Pattern[] { z3Ctx.mkPattern(eqLhs) };
      BoolExpr axiom = z3Ctx.mkForall(bound, body, 1, patterns, null, null, null);
      solver.add(axiom);
    }
  }

  private void loadAxiomProjTuple(Solver solver) {
    FuncDecl funcGetCell = getFunctionByName("get-cell");
    FuncDecl funcProjTuple = getFunctionByName("proj-tuple");
    // proj-tuple_nil
    // proj-tuple([], t) = []
    {
      Expr[] bound = new Expr[1];
      bound[0] = z3Ctx.mkConst("t", tupleSort);
      Expr eqLhs = funcProjTuple.apply(attrListSort.getNil(), bound[0]);
      BoolExpr body = z3Ctx.mkEq(eqLhs, tupleSort.getNil());
      Pattern[] patterns = new Pattern[] { z3Ctx.mkPattern(eqLhs) };
      BoolExpr axiom = z3Ctx.mkForall(bound, body, 1, patterns, null, null, null);
      solver.add(axiom);
    }
    // proj-tuple_ind
    // proj-tuple(a :: l, t) = get-cell(a, t) :: proj-tuple(l, t)
    {
      Expr[] bound = new Expr[3];
      bound[0] = z3Ctx.mkConst("a", attrSort);
      bound[1] = z3Ctx.mkConst("l", attrListSort);
      bound[2] = z3Ctx.mkConst("t", tupleSort);
      Expr eqLhs = funcProjTuple.apply(attrListSort.getConsDecl().apply(bound[0], bound[1]),
          bound[2]);
      Expr eqRhs = tupleSort.getConsDecl().apply(funcGetCell.apply(bound[0], bound[2]),
          funcProjTuple.apply(bound[1], bound[2]));
      BoolExpr body = z3Ctx.mkEq(eqLhs, eqRhs);
      Pattern[] patterns = new Pattern[] { z3Ctx.mkPattern(eqLhs) };
      BoolExpr axiom = z3Ctx.mkForall(bound, body, 1, patterns, null, null, null);
      solver.add(axiom);
    }
  }

  private void loadAxiomProj(Solver solver) {
    FuncDecl funcProjTuple = getFunctionByName("proj-tuple");
    FuncDecl funcProj = getFunctionByName("proj");
    // proj_nil
    // proj(l, []) = []
    {
      Expr[] bound = new Expr[1];
      bound[0] = z3Ctx.mkConst("l", attrListSort);
      Expr eqLhs = funcProj.apply(bound[0], relSort.getNil());
      BoolExpr body = z3Ctx.mkEq(eqLhs, relSort.getNil());
      Pattern[] patterns = new Pattern[] { z3Ctx.mkPattern(eqLhs) };
      BoolExpr axiom = z3Ctx.mkForall(bound, body, 1, patterns, null, null, null);
      solver.add(axiom);
    }
    // proj_ind
    // proj(l, t :: r) = proj-tuple(l, t) :: proj(l, r)
    {
      Expr[] bound = new Expr[3];
      bound[0] = z3Ctx.mkConst("l", attrListSort);
      bound[1] = z3Ctx.mkConst("t", tupleSort);
      bound[2] = z3Ctx.mkConst("r", relSort);
      Expr eqLhs = funcProj.apply(bound[0], relSort.getConsDecl().apply(bound[1], bound[2]));
      Expr eqRhs = relSort.getConsDecl().apply(funcProjTuple.apply(bound[0], bound[1]),
          funcProj.apply(bound[0], bound[2]));
      BoolExpr body = z3Ctx.mkEq(eqLhs, eqRhs);
      Pattern[] patterns = new Pattern[] { z3Ctx.mkPattern(eqLhs) };
      BoolExpr axiom = z3Ctx.mkForall(bound, body, 1, patterns, null, null, null);
      solver.add(axiom);
    }
  }

  private void loadAxiomSelEq(Solver solver) {
    FuncDecl funcGetCell = getFunctionByName("get-cell");
    FuncDecl funcSelEq = getFunctionByName("sel-eq");
    // sel-eq_nil
    // sel-eq(a1, a2, []) = []
    {
      Expr[] bound = new Expr[2];
      bound[0] = z3Ctx.mkConst("a1", attrSort);
      bound[1] = z3Ctx.mkConst("a2", attrSort);
      Expr eqLhs = funcSelEq.apply(bound[0], bound[1], relSort.getNil());
      BoolExpr body = z3Ctx.mkEq(eqLhs, relSort.getNil());
      Pattern[] patterns = new Pattern[] { z3Ctx.mkPattern(eqLhs) };
      BoolExpr axiom = z3Ctx.mkForall(bound, body, 1, patterns, null, null, null);
      solver.add(axiom);
    }
    // sel-eq_ind
    // sel-eq(a1, a2, t :: r) = (t.a1 == t.a2) ? t :: sel-eq(a1, a2, r) : sel-eq(a1, a2, r)
    {
      Expr[] bound = new Expr[4];
      bound[0] = z3Ctx.mkConst("a1", attrSort);
      bound[1] = z3Ctx.mkConst("a2", attrSort);
      bound[2] = z3Ctx.mkConst("t", tupleSort);
      bound[3] = z3Ctx.mkConst("r", relSort);
      Expr eqLhs = funcSelEq.apply(bound[0], bound[1],
          relSort.getConsDecl().apply(bound[2], bound[3]));
      Expr eqRhs = z3Ctx.mkITE(
          z3Ctx.mkEq(funcGetCell.apply(bound[0], bound[2]), funcGetCell.apply(bound[1], bound[2])),
          relSort.getConsDecl().apply(bound[2], funcSelEq.apply(bound[0], bound[1], bound[3])),
          funcSelEq.apply(bound[0], bound[1], bound[3]));
      BoolExpr body = z3Ctx.mkEq(eqLhs, eqRhs);
      Pattern[] patterns = new Pattern[] { z3Ctx.mkPattern(eqLhs) };
      BoolExpr axiom = z3Ctx.mkForall(bound, body, 1, patterns, null, null, null);
      solver.add(axiom);
    }
  }

  private void loadAxiomSelEqv(Solver solver) {
    FuncDecl funcGetCell = getFunctionByName("get-cell");
    FuncDecl funcSelEqv = getFunctionByName("sel-eqv");
    // sel-eqv_nil
    // sel-eqv(a, v, []) = []
    {
      Expr[] bound = new Expr[2];
      bound[0] = z3Ctx.mkConst("a", attrSort);
      bound[1] = z3Ctx.mkConst("v", valSort);
      Expr eqLhs = funcSelEqv.apply(bound[0], bound[1], relSort.getNil());
      BoolExpr body = z3Ctx.mkEq(eqLhs, relSort.getNil());
      Pattern[] patterns = new Pattern[] { z3Ctx.mkPattern(eqLhs) };
      BoolExpr axiom = z3Ctx.mkForall(bound, body, 1, patterns, null, null, null);
      solver.add(axiom);
    }
    // sel-eqv_ind
    // sel-eqv(a, v, t :: r) = (t.a == v) ? t :: sel-eq(a1, a2, r) : sel-eq(a1, a2, r)
    {
      Expr[] bound = new Expr[4];
      bound[0] = z3Ctx.mkConst("a", attrSort);
      bound[1] = z3Ctx.mkConst("v", valSort);
      bound[2] = z3Ctx.mkConst("t", tupleSort);
      bound[3] = z3Ctx.mkConst("r", relSort);
      Expr eqLhs = funcSelEqv.apply(bound[0], bound[1],
          relSort.getConsDecl().apply(bound[2], bound[3]));
      Expr eqRhs = z3Ctx.mkITE(z3Ctx.mkEq(funcGetCell.apply(bound[0], bound[2]), bound[1]),
          relSort.getConsDecl().apply(bound[2], funcSelEqv.apply(bound[0], bound[1], bound[3])),
          funcSelEqv.apply(bound[0], bound[1], bound[3]));
      BoolExpr body = z3Ctx.mkEq(eqLhs, eqRhs);
      Pattern[] patterns = new Pattern[] { z3Ctx.mkPattern(eqLhs) };
      BoolExpr axiom = z3Ctx.mkForall(bound, body, 1, patterns, null, null, null);
      solver.add(axiom);
    }
  }

  private void loadAxiomEquiJoin(Solver solver) {
    FuncDecl funcSelEq = getFunctionByName("sel-eq");
    FuncDecl funcProd = getFunctionByName("prod");
    FuncDecl funcEquiJoin = getFunctionByName("equi-join");
    // equi-join_def
    // equi-join(a1, a2, r1, r2) = sel-eq(a1, a2, prod(r1, r2))
    {
      Expr[] bound = new Expr[4];
      bound[0] = z3Ctx.mkConst("a1", attrSort);
      bound[1] = z3Ctx.mkConst("a2", attrSort);
      bound[2] = z3Ctx.mkConst("r1", relSort);
      bound[3] = z3Ctx.mkConst("r2", relSort);
      Expr eqLhs = funcEquiJoin.apply(bound[0], bound[1], bound[2], bound[3]);
      Expr eqRhs = funcSelEq.apply(bound[0], bound[1], funcProd.apply(bound[2], bound[3]));
      BoolExpr body = z3Ctx.mkEq(eqLhs, eqRhs);
      Pattern[] patterns = new Pattern[] { z3Ctx.mkPattern(eqLhs) };
      BoolExpr axiom = z3Ctx.mkForall(bound, body, 1, patterns, null, null, null);
      solver.add(axiom);
    }
  }

  private void loadAxiomMinusOne(Solver solver) {
    FuncDecl funcMinusOne = getFunctionByName("minus-one");
    // minus-one_nil
    // minus-one([], t) = []
    {
      Expr[] bound = new Expr[1];
      bound[0] = z3Ctx.mkConst("t", tupleSort);
      Expr eqLhs = funcMinusOne.apply(relSort.getNil(), bound[0]);
      BoolExpr body = z3Ctx.mkEq(eqLhs, relSort.getNil());
      Pattern[] patterns = new Pattern[] { z3Ctx.mkPattern(eqLhs) };
      BoolExpr axiom = z3Ctx.mkForall(bound, body, 1, patterns, null, null, null);
      solver.add(axiom);
    }
    // minus-one_ind
    // minus-one(t1 :: r1, t2) = (t1 == t2) ? r1 : t1 :: minus-one(r1, t2)
    {
      Expr[] bound = new Expr[3];
      bound[0] = z3Ctx.mkConst("t1", tupleSort);
      bound[1] = z3Ctx.mkConst("r1", relSort);
      bound[2] = z3Ctx.mkConst("t2", tupleSort);
      Expr eqLhs = funcMinusOne.apply(relSort.getConsDecl().apply(bound[0], bound[1]), bound[2]);
      Expr eqRhs = z3Ctx.mkITE(z3Ctx.mkEq(bound[0], bound[2]), bound[1],
          relSort.getConsDecl().apply(bound[0], funcMinusOne.apply(bound[1], bound[2])));
      BoolExpr body = z3Ctx.mkEq(eqLhs, eqRhs);
      Pattern[] patterns = new Pattern[] { z3Ctx.mkPattern(eqLhs) };
      BoolExpr axiom = z3Ctx.mkForall(bound, body, 1, patterns, null, null, null);
      solver.add(axiom);
    }
  }

  private void loadAxiomMinus(Solver solver) {
    FuncDecl funcMinusOne = getFunctionByName("minus-one");
    FuncDecl funcMinus = getFunctionByName("minus");
    // minus_nil
    // minus(r1, []) = r1
    {
      Expr[] bound = new Expr[1];
      bound[0] = z3Ctx.mkConst("r1", relSort);
      Expr eqLhs = funcMinus.apply(bound[0], relSort.getNil());
      BoolExpr body = z3Ctx.mkEq(eqLhs, bound[0]);
      Pattern[] patterns = new Pattern[] { z3Ctx.mkPattern(eqLhs) };
      BoolExpr axiom = z3Ctx.mkForall(bound, body, 1, patterns, null, null, null);
      solver.add(axiom);
    }
    // minus_ind
    // minus(r1, t :: r2) = minus(minus-one(r1, t), r2)
    {
      Expr[] bound = new Expr[3];
      bound[0] = z3Ctx.mkConst("r1", relSort);
      bound[1] = z3Ctx.mkConst("t", tupleSort);
      bound[2] = z3Ctx.mkConst("r2", relSort);
      Expr eqLhs = funcMinus.apply(bound[0], relSort.getConsDecl().apply(bound[1], bound[2]));
      Expr eqRhs = funcMinus.apply(funcMinusOne.apply(bound[0], bound[1]), bound[2]);
      BoolExpr body = z3Ctx.mkEq(eqLhs, eqRhs);
      Pattern[] patterns = new Pattern[] { z3Ctx.mkPattern(eqLhs) };
      BoolExpr axiom = z3Ctx.mkForall(bound, body, 1, patterns, null, null, null);
      solver.add(axiom);
    }
  }

  private void loadAxiomUpdTuple(Solver solver) {
    FuncDecl funcUpdTuple = getFunctionByName("upd-tuple");
    // upd-tuple_nil
    // upd-tuple([], a, v) = []
    {
      Expr[] bound = new Expr[2];
      bound[0] = z3Ctx.mkConst("a", attrSort);
      bound[1] = z3Ctx.mkConst("v", valSort);
      Expr eqLhs = funcUpdTuple.apply(tupleSort.getNil(), bound[0], bound[1]);
      BoolExpr body = z3Ctx.mkEq(eqLhs, tupleSort.getNil());
      Pattern[] patterns = new Pattern[] { z3Ctx.mkPattern(eqLhs) };
      BoolExpr axiom = z3Ctx.mkForall(bound, body, 1, patterns, null, null, null);
      solver.add(axiom);
    }
    // upd-tuple_ind
    // upd-tuple(c :: t, a, v) = (a == 0) ? (v :: t) : (c :: upd-tuple(t, a-1, v))
    {
      Expr[] bound = new Expr[4];
      bound[0] = z3Ctx.mkConst("c", cellSort);
      bound[1] = z3Ctx.mkConst("t", tupleSort);
      bound[2] = z3Ctx.mkConst("a", attrSort);
      bound[3] = z3Ctx.mkConst("v", valSort);
      Expr eqLhs = funcUpdTuple.apply(tupleSort.getConsDecl().apply(bound[0], bound[1]), bound[2],
          bound[3]);
      Expr eqRhs = z3Ctx.mkITE(z3Ctx.mkEq(bound[2], z3Ctx.mkInt(0)),
          tupleSort.getConsDecl().apply(bound[3], bound[1]),
          tupleSort.getConsDecl().apply(bound[0], funcUpdTuple.apply(bound[1],
              z3Ctx.mkSub((ArithExpr) bound[2], z3Ctx.mkInt(1)), bound[3])));
      BoolExpr body = z3Ctx.mkEq(eqLhs, eqRhs);
      Pattern[] patterns = new Pattern[] { z3Ctx.mkPattern(eqLhs) };
      BoolExpr axiom = z3Ctx.mkForall(bound, body, 1, patterns, null, null, null);
      solver.add(axiom);
    }
  }

  private void loadAxiomUpd(Solver solver) {
    FuncDecl funcUpdTuple = getFunctionByName("upd-tuple");
    FuncDecl funcUpd = getFunctionByName("upd");
    // upd_nil
    // upd([], a, v) = []
    {
      Expr[] bound = new Expr[2];
      bound[0] = z3Ctx.mkConst("a", attrSort);
      bound[1] = z3Ctx.mkConst("v", valSort);
      Expr eqLhs = funcUpd.apply(relSort.getNil(), bound[0], bound[1]);
      BoolExpr body = z3Ctx.mkEq(eqLhs, relSort.getNil());
      Pattern[] patterns = new Pattern[] { z3Ctx.mkPattern(eqLhs) };
      BoolExpr axiom = z3Ctx.mkForall(bound, body, 1, patterns, null, null, null);
      solver.add(axiom);
    }
    // upd_ind
    // upd(t :: r, a, v) = upd-tuple(t, a, v) :: upd(r, a, v)
    {
      Expr[] bound = new Expr[4];
      bound[0] = z3Ctx.mkConst("t", tupleSort);
      bound[1] = z3Ctx.mkConst("r", relSort);
      bound[2] = z3Ctx.mkConst("a", attrSort);
      bound[3] = z3Ctx.mkConst("v", valSort);
      Expr eqLhs = funcUpd.apply(relSort.getConsDecl().apply(bound[0], bound[1]), bound[2],
          bound[3]);
      Expr eqRhs = relSort.getConsDecl().apply(funcUpdTuple.apply(bound[0], bound[2], bound[3]),
          funcUpd.apply(bound[1], bound[2], bound[3]));
      BoolExpr body = z3Ctx.mkEq(eqLhs, eqRhs);
      Pattern[] patterns = new Pattern[] { z3Ctx.mkPattern(eqLhs) };
      BoolExpr axiom = z3Ctx.mkForall(bound, body, 1, patterns, null, null, null);
      solver.add(axiom);
    }
  }

  private void loadAxiomMemList(Solver solver) {
    FuncDecl funcMemList = getFunctionByName("mem-list");
    // mem-list_nil
    // mem-list(a, []) = false
    {
      Expr[] bound = new Expr[1];
      bound[0] = z3Ctx.mkConst("a", attrSort);
      Expr eqLhs = funcMemList.apply(bound[0], attrListSort.getNil());
      BoolExpr body = z3Ctx.mkEq(eqLhs, z3Ctx.mkFalse());
      Pattern[] patterns = new Pattern[] { z3Ctx.mkPattern(eqLhs) };
      BoolExpr axiom = z3Ctx.mkForall(bound, body, 1, patterns, null, null, null);
      solver.add(axiom);
    }
    // mem-list_ind
    // mem-list(a, h :: t) = (a == h) ? true : mem-list(a, t)
    {
      Expr[] bound = new Expr[3];
      bound[0] = z3Ctx.mkConst("a", attrSort);
      bound[1] = z3Ctx.mkConst("h", attrSort);
      bound[2] = z3Ctx.mkConst("t", attrListSort);
      Expr eqLhs = funcMemList.apply(bound[0],
          attrListSort.getConsDecl().apply(bound[1], bound[2]));
      Expr eqRhs = z3Ctx.mkITE(z3Ctx.mkEq(bound[0], bound[1]), z3Ctx.mkTrue(),
          funcMemList.apply(bound[0], bound[2]));
      BoolExpr body = z3Ctx.mkEq(eqLhs, eqRhs);
      Pattern[] patterns = new Pattern[] { z3Ctx.mkPattern(eqLhs) };
      BoolExpr axiom = z3Ctx.mkForall(bound, body, 1, patterns, null, null, null);
      solver.add(axiom);
    }
  }

  private void loadTheoremAppNil(Solver solver) {
    FuncDecl funcAppend = getFunctionByName("append");
    // app_nil_r
    // append(r, []) = r
    {
      Expr[] bound = new Expr[1];
      bound[0] = z3Ctx.mkConst("r", relSort);
      Expr eqLhs = funcAppend.apply(bound[0], relSort.getNil());
      BoolExpr body = z3Ctx.mkEq(eqLhs, bound[0]);
      Pattern[] patterns = new Pattern[] { z3Ctx.mkPattern(eqLhs) };
      BoolExpr theorem = z3Ctx.mkForall(bound, body, 1, patterns, null, null, null);
      solver.add(theorem);
    }
  }

  private void loadTheoremJoinNil(Solver solver) {
    FuncDecl funcEquiJoin = getFunctionByName("equi-join");
    // equi-join_nil_r
    // equi-join(r, []) = []
    {
      Expr[] bound = new Expr[3];
      bound[0] = z3Ctx.mkConst("a1", attrSort);
      bound[1] = z3Ctx.mkConst("a2", attrSort);
      bound[2] = z3Ctx.mkConst("r", relSort);
      Expr eqLhs = funcEquiJoin.apply(bound[0], bound[1], bound[2], relSort.getNil());
      Expr eqRhs = relSort.getNil();
      BoolExpr body = z3Ctx.mkEq(eqLhs, eqRhs);
      Pattern[] patterns = new Pattern[] { z3Ctx.mkPattern(eqLhs) };
      BoolExpr theorem = z3Ctx.mkForall(bound, body, 1, patterns, null, null, null);
      solver.add(theorem);
    }
  }

  private void loadTheoremMinusSame(Solver solver) {
    FuncDecl funcMinus = getFunctionByName("minus");
    // minus_same
    // minus(r, r) = []
    {
      Expr[] bound = new Expr[1];
      bound[0] = z3Ctx.mkConst("r", relSort);
      Expr eqLhs = funcMinus.apply(bound[0], bound[0]);
      Expr eqRhs = relSort.getNil();
      BoolExpr body = z3Ctx.mkEq(eqLhs, eqRhs);
      Pattern[] patterns = new Pattern[] { z3Ctx.mkPattern(eqLhs) };
      BoolExpr theorem = z3Ctx.mkForall(bound, body, 1, patterns, null, null, null);
      solver.add(theorem);
    }
  }

  private void loadTheoremSelSubSet(Solver solver) {
    FuncDecl funcSubSet = getFunctionByName("sub-set");
    FuncDecl funcSelEqv = getFunctionByName("sel-eqv");
    FuncDecl funcSelInEqv = getFunctionByName("sel-in-eqv");
    // sub-set_sel-eqv
    // sub-set(sel-eqv(a, v, r), r) = true
    {
      Expr[] bound = new Expr[3];
      bound[0] = z3Ctx.mkConst("a", attrSort);
      bound[1] = z3Ctx.mkConst("v", valSort);
      bound[2] = z3Ctx.mkConst("r", relSort);
      Expr sel = funcSelEqv.apply(bound[0], bound[1], bound[2]);
      Expr eqLhs = funcSubSet.apply(sel, bound[2]);
      Expr eqRhs = z3Ctx.mkTrue();
      BoolExpr body = z3Ctx.mkEq(eqLhs, eqRhs);
      Pattern[] patterns = new Pattern[] { z3Ctx.mkPattern(sel) };
      BoolExpr theorem = z3Ctx.mkForall(bound, body, 1, patterns, null, null, null);
      solver.add(theorem);
    }
    // sub-set_sel-in-eqv
    // sub-set(sel-in-eqv(a, v, r1, r2), r2) = true
    {
      Expr[] bound = new Expr[4];
      bound[0] = z3Ctx.mkConst("a", attrSort);
      bound[1] = z3Ctx.mkConst("v", valSort);
      bound[2] = z3Ctx.mkConst("r1", relSort);
      bound[3] = z3Ctx.mkConst("r2", relSort);
      Expr sel = funcSelInEqv.apply(bound[0], bound[1], bound[2], bound[3]);
      Expr eqLhs = funcSubSet.apply(sel, bound[3]);
      Expr eqRhs = z3Ctx.mkTrue();
      BoolExpr body = z3Ctx.mkEq(eqLhs, eqRhs);
      Pattern[] patterns = new Pattern[] { z3Ctx.mkPattern(sel) };
      BoolExpr theorem = z3Ctx.mkForall(bound, body, 1, patterns, null, null, null);
      solver.add(theorem);
    }
  }

  private void loadTheoremProjSubSet(Solver solver) {
    FuncDecl funcSubSet = getFunctionByName("sub-set");
    FuncDecl funcProj = getFunctionByName("proj");
    // sub-set_proj
    // sub-set(r2, r1) = true => sub-set(proj(l, r2), proj(l, r1)) = true
    {
      Expr[] bound = new Expr[3];
      bound[0] = z3Ctx.mkConst("l", attrListSort);
      bound[1] = z3Ctx.mkConst("r1", relSort);
      bound[2] = z3Ctx.mkConst("r2", relSort);
      BoolExpr implyLhs = z3Ctx.mkEq(funcSubSet.apply(bound[2], bound[1]), z3Ctx.mkTrue());
      BoolExpr implyRhs = z3Ctx.mkEq(
          funcSubSet.apply(funcProj.apply(bound[0], bound[2]), funcProj.apply(bound[0], bound[1])),
          z3Ctx.mkTrue());
      BoolExpr body = z3Ctx.mkImplies(implyLhs, implyRhs);
      BoolExpr theorem = z3Ctx.mkForall(bound, body, 1, null, null, null, null);
      solver.add(theorem);
    }
  }

  private void loadTheoremAppMinusSimpl(Solver solver) {
    FuncDecl funcSubSet = getFunctionByName("sub-set");
    FuncDecl funcAppend = getFunctionByName("append");
    FuncDecl funcMinus = getFunctionByName("minus");
    // minus_app_simpl
    // sub-set(r2, r1) = true => r1 - r2 + r2 = r1
    {
      Expr[] bound = new Expr[2];
      bound[0] = z3Ctx.mkConst("r1", relSort);
      bound[1] = z3Ctx.mkConst("r2", relSort);
      BoolExpr premise = z3Ctx.mkEq(funcSubSet.apply(bound[1], bound[0]), z3Ctx.mkTrue());
      Expr eqLhs = funcAppend.apply(funcMinus.apply(bound[0], bound[1]), bound[1]);
      BoolExpr body = z3Ctx.mkImplies(premise, z3Ctx.mkEq(eqLhs, bound[0]));
      Pattern[] patterns = new Pattern[] { z3Ctx.mkPattern(eqLhs) };
      BoolExpr theorem = z3Ctx.mkForall(bound, body, 1, patterns, null, null, null);
      solver.add(theorem);
    }
    // app_minus_simpl
    // r1 + r2 - r2 = r1
    {
      Expr[] bound = new Expr[2];
      bound[0] = z3Ctx.mkConst("r1", relSort);
      bound[1] = z3Ctx.mkConst("r2", relSort);
      Expr eqLhs = funcMinus.apply(funcAppend.apply(bound[0], bound[1]), bound[1]);
      BoolExpr body = z3Ctx.mkEq(eqLhs, bound[0]);
      Pattern[] patterns = new Pattern[] { z3Ctx.mkPattern(eqLhs) };
      BoolExpr theorem = z3Ctx.mkForall(bound, body, 1, patterns, null, null, null);
      solver.add(theorem);
    }
  }

  private void loadTheoremSelInSimpl(Solver solver) {
    FuncDecl funcEquiJoin = getFunctionByName("equi-join");
    FuncDecl funcSelInEqv = getFunctionByName("sel-in-eqv");
    FuncDecl funcSelEqv = getFunctionByName("sel-eqv");
    // join_sel-in_simpl_r
    // equi-join(a1, a2, r1, sel-in-eqv(a, v, r1, r2))
    // = sel-eqv(a, v, equi-join(a1, a2, r1, r2))
    {
      Expr[] bound = new Expr[6];
      bound[0] = z3Ctx.mkConst("a1", attrSort);
      bound[1] = z3Ctx.mkConst("a2", attrSort);
      bound[2] = z3Ctx.mkConst("a", attrSort);
      bound[3] = z3Ctx.mkConst("v", valSort);
      bound[4] = z3Ctx.mkConst("r1", relSort);
      bound[5] = z3Ctx.mkConst("r2", relSort);
      Expr eqLhs = funcEquiJoin.apply(bound[0], bound[1], bound[4],
          funcSelInEqv.apply(bound[2], bound[3], bound[4], bound[5]));
      Expr eqRhs = funcSelEqv.apply(bound[2], bound[3],
          funcEquiJoin.apply(bound[0], bound[1], bound[4], bound[5]));
      BoolExpr body = z3Ctx.mkEq(eqLhs, eqRhs);
      Pattern[] patterns = new Pattern[] { z3Ctx.mkPattern(eqLhs) };
      BoolExpr theorem = z3Ctx.mkForall(bound, body, 1, patterns, null, null, null);
      solver.add(theorem);
    }
    // join_sel-in_simpl_l
    // equi-join(a1, a2, sel-in-eqv(a, v, r1, r2), r1)
    // = sel-eqv(a, v, equi-join(a1, a2, r1, r2))
    {
      Expr[] bound = new Expr[6];
      bound[0] = z3Ctx.mkConst("a1", attrSort);
      bound[1] = z3Ctx.mkConst("a2", attrSort);
      bound[2] = z3Ctx.mkConst("a", attrSort);
      bound[3] = z3Ctx.mkConst("v", valSort);
      bound[4] = z3Ctx.mkConst("r1", relSort);
      bound[5] = z3Ctx.mkConst("r2", relSort);
      Expr eqLhs = funcEquiJoin.apply(bound[0], bound[1],
          funcSelInEqv.apply(bound[2], bound[3], bound[4], bound[5]), bound[4]);
      Expr eqRhs = funcSelEqv.apply(bound[2], bound[3],
          funcEquiJoin.apply(bound[0], bound[1], bound[4], bound[5]));
      BoolExpr body = z3Ctx.mkEq(eqLhs, eqRhs);
      Pattern[] patterns = new Pattern[] { z3Ctx.mkPattern(eqLhs) };
      BoolExpr theorem = z3Ctx.mkForall(bound, body, 1, patterns, null, null, null);
      solver.add(theorem);
    }
  }

  private void loadTheoremSelIdem(Solver solver) {
    FuncDecl funcSelEqv = getFunctionByName("sel-eqv");
    // sel-eqv_idem
    // sel-eqv(a, v, sel-eqv(a, v, r)) = sel-eqv(a, v, r)
    {
      Expr[] bound = new Expr[3];
      bound[0] = z3Ctx.mkConst("a", attrSort);
      bound[1] = z3Ctx.mkConst("v", valSort);
      bound[2] = z3Ctx.mkConst("r", relSort);
      Expr eqLhs = funcSelEqv.apply(bound[0], bound[1],
          funcSelEqv.apply(bound[0], bound[1], bound[2]));
      Expr eqRhs = funcSelEqv.apply(bound[0], bound[1], bound[2]);
      BoolExpr body = z3Ctx.mkEq(eqLhs, eqRhs);
      Pattern[] patterns = new Pattern[] { z3Ctx.mkPattern(eqLhs) };
      BoolExpr theorem = z3Ctx.mkForall(bound, body, 1, patterns, null, null, null);
      solver.add(theorem);
    }
  }

  private void loadTheoremJoinAppDist(Solver solver) {
    FuncDecl funcEquiJoin = getFunctionByName("equi-join");
    FuncDecl funcAppend = getFunctionByName("append");
    // join_app_dist_r
    // equi-join(a1, a2, r1, append(r2, r3))
    // = append(equi-join(a1, a2, r1, r2), equi-join(a1, a2, r1, r3))
    {
      Expr[] bound = new Expr[5];
      bound[0] = z3Ctx.mkConst("a1", attrSort);
      bound[1] = z3Ctx.mkConst("a2", attrSort);
      bound[2] = z3Ctx.mkConst("r1", relSort);
      bound[3] = z3Ctx.mkConst("r2", relSort);
      bound[4] = z3Ctx.mkConst("r3", relSort);
      Expr eqLhs = funcEquiJoin.apply(bound[0], bound[1], bound[2],
          funcAppend.apply(bound[3], bound[4]));
      Expr eqRhs = funcAppend.apply(funcEquiJoin.apply(bound[0], bound[1], bound[2], bound[3]),
          funcEquiJoin.apply(bound[0], bound[1], bound[2], bound[4]));
      BoolExpr body = z3Ctx.mkEq(eqLhs, eqRhs);
      Pattern[] patterns = new Pattern[] { z3Ctx.mkPattern(eqLhs) };
      BoolExpr theorem = z3Ctx.mkForall(bound, body, 1, patterns, null, null, null);
      solver.add(theorem);
    }
    // join_app_dist_l
    // equi-join(a1, a2, append(r1, r2), r3)
    // = append(equi-join(a1, a2, r1, r3), equi-join(a1, a2, r2, r3))
    {
      Expr[] bound = new Expr[5];
      bound[0] = z3Ctx.mkConst("a1", attrSort);
      bound[1] = z3Ctx.mkConst("a2", attrSort);
      bound[2] = z3Ctx.mkConst("r1", relSort);
      bound[3] = z3Ctx.mkConst("r2", relSort);
      bound[4] = z3Ctx.mkConst("r3", relSort);
      Expr eqLhs = funcEquiJoin.apply(bound[0], bound[1], funcAppend.apply(bound[2], bound[3]),
          bound[4]);
      Expr eqRhs = funcAppend.apply(funcEquiJoin.apply(bound[0], bound[1], bound[2], bound[4]),
          funcEquiJoin.apply(bound[0], bound[1], bound[3], bound[4]));
      BoolExpr body = z3Ctx.mkEq(eqLhs, eqRhs);
      Pattern[] patterns = new Pattern[] { z3Ctx.mkPattern(eqLhs) };
      BoolExpr theorem = z3Ctx.mkForall(bound, body, 1, patterns, null, null, null);
      solver.add(theorem);
    }
  }

  private void loadTheoremProjAppDist(Solver solver) {
    FuncDecl funcProj = getFunctionByName("proj");
    FuncDecl funcAppend = getFunctionByName("append");
    // proj_app_dist
    // proj(l, append(r1, r2)) = append(proj(l, r1), proj(l, r2))
    {
      Expr[] bound = new Expr[3];
      bound[0] = z3Ctx.mkConst("l", attrListSort);
      bound[1] = z3Ctx.mkConst("r1", relSort);
      bound[2] = z3Ctx.mkConst("r2", relSort);
      Expr eqLhs = funcProj.apply(bound[0], funcAppend.apply(bound[1], bound[2]));
      Expr eqRhs = funcAppend.apply(funcProj.apply(bound[0], bound[1]),
          funcProj.apply(bound[0], bound[2]));
      BoolExpr body = z3Ctx.mkEq(eqLhs, eqRhs);
      Pattern[] patterns = new Pattern[] { z3Ctx.mkPattern(eqLhs) };
      BoolExpr theorem = z3Ctx.mkForall(bound, body, 1, patterns, null, null, null);
      solver.add(theorem);
    }
  }

  private void loadTheoremJoinMinusDist(Solver solver) {
    FuncDecl funcSubSet = getFunctionByName("sub-set");
    FuncDecl funcEquiJoin = getFunctionByName("equi-join");
    FuncDecl funcMinus = getFunctionByName("minus");
    // join_minus_dist_l
    // sub-set(r2, r1) = true =>
    // equi-join(a1, a2, minus(r1, r2), r3)
    // = minus(equi-join(a1, a2, r1, r3), equi-join(a1, a2, r2, r3))
    {
      Expr[] bound = new Expr[5];
      bound[0] = z3Ctx.mkConst("a1", attrSort);
      bound[1] = z3Ctx.mkConst("a2", attrSort);
      bound[2] = z3Ctx.mkConst("r1", relSort);
      bound[3] = z3Ctx.mkConst("r2", relSort);
      bound[4] = z3Ctx.mkConst("r3", relSort);
      BoolExpr premise = z3Ctx.mkEq(funcSubSet.apply(bound[3], bound[2]), z3Ctx.mkTrue());
      Expr eqLhs = funcEquiJoin.apply(bound[0], bound[1], funcMinus.apply(bound[2], bound[3]),
          bound[4]);
      Expr eqRhs = funcMinus.apply(funcEquiJoin.apply(bound[0], bound[1], bound[2], bound[4]),
          funcEquiJoin.apply(bound[0], bound[1], bound[3], bound[4]));
      BoolExpr body = z3Ctx.mkImplies(premise, z3Ctx.mkEq(eqLhs, eqRhs));
      Pattern[] patterns = new Pattern[] { z3Ctx.mkPattern(eqLhs) };
      BoolExpr theorem = z3Ctx.mkForall(bound, body, 1, patterns, null, null, null);
      solver.add(theorem);
    }
    // join_minus_dist_r
    // sub-set(r3, r2) = true =>
    // equi-join(a1, a2, r1, minus(r2, r3))
    // = minus(equi-join(a1, a2, r1, r2), equi-join(a1, a2, r1, r3))
    {
      Expr[] bound = new Expr[5];
      bound[0] = z3Ctx.mkConst("a1", attrSort);
      bound[1] = z3Ctx.mkConst("a2", attrSort);
      bound[2] = z3Ctx.mkConst("r1", relSort);
      bound[3] = z3Ctx.mkConst("r2", relSort);
      bound[4] = z3Ctx.mkConst("r3", relSort);
      BoolExpr premise = z3Ctx.mkEq(funcSubSet.apply(bound[4], bound[3]), z3Ctx.mkTrue());
      Expr eqLhs = funcEquiJoin.apply(bound[0], bound[1], bound[2],
          funcMinus.apply(bound[3], bound[4]));
      Expr eqRhs = funcMinus.apply(funcEquiJoin.apply(bound[0], bound[1], bound[2], bound[3]),
          funcEquiJoin.apply(bound[0], bound[1], bound[2], bound[4]));
      BoolExpr body = z3Ctx.mkImplies(premise, z3Ctx.mkEq(eqLhs, eqRhs));
      Pattern[] patterns = new Pattern[] { z3Ctx.mkPattern(eqLhs) };
      BoolExpr theorem = z3Ctx.mkForall(bound, body, 1, patterns, null, null, null);
      solver.add(theorem);
    }
  }

  private void loadTheoremProjMinusDist(Solver solver) {
    FuncDecl funcSubSet = getFunctionByName("sub-set");
    FuncDecl funcProj = getFunctionByName("proj");
    FuncDecl funcMinus = getFunctionByName("minus");
    // proj_minus_dist
    // sub-set(r2, r1) = true =>
    // proj(l, minus(r1, r2)) = minus(proj(l, r1), proj(l, r2))
    {
      Expr[] bound = new Expr[3];
      bound[0] = z3Ctx.mkConst("l", attrListSort);
      bound[1] = z3Ctx.mkConst("r1", relSort);
      bound[2] = z3Ctx.mkConst("r2", relSort);
      BoolExpr premise = z3Ctx.mkEq(funcSubSet.apply(bound[2], bound[1]), z3Ctx.mkTrue());
      Expr eqLhs = funcProj.apply(bound[0], funcMinus.apply(bound[1], bound[2]));
      Expr eqRhs = funcMinus.apply(funcProj.apply(bound[0], bound[1]),
          funcProj.apply(bound[0], bound[2]));
      BoolExpr body = z3Ctx.mkImplies(premise, z3Ctx.mkEq(eqLhs, eqRhs));
      Pattern[] patterns = new Pattern[] { z3Ctx.mkPattern(eqLhs) };
      BoolExpr theorem = z3Ctx.mkForall(bound, body, 1, patterns, null, null, null);
      solver.add(theorem);
    }
  }

  private void loadTheoremProjSubseq(Solver solver) {
    // head & tail instantiation
    // TODO full-version: same-subseq
    FuncDecl funcProj = getFunctionByName("proj");
    // proj_subseq_t
    // proj(l1, r1) = proj(l2, r2) => proj(tail(l1), r1) = proj(tail(l2), r2)
    {
      Expr[] bound = new Expr[4];
      bound[0] = z3Ctx.mkConst("l1", attrListSort);
      bound[1] = z3Ctx.mkConst("l2", attrListSort);
      bound[2] = z3Ctx.mkConst("r1", relSort);
      bound[3] = z3Ctx.mkConst("r2", relSort);
      BoolExpr implyLhs = z3Ctx.mkEq(funcProj.apply(bound[0], bound[2]),
          funcProj.apply(bound[1], bound[3]));
      BoolExpr implyRhs = z3Ctx.mkEq(
          funcProj.apply(attrListSort.getTailDecl().apply(bound[0]), bound[2]),
          funcProj.apply(attrListSort.getTailDecl().apply(bound[1]), bound[3]));
      BoolExpr body = z3Ctx.mkImplies(implyLhs, implyRhs);
      BoolExpr theorem = z3Ctx.mkForall(bound, body, 1, null, null, null, null);
      solver.add(theorem);
    }
    // proj_subseq_h
    // proj(l1, r1) = proj(l2, r2) => proj([head l1], r1) = proj([head l2], r2)
    {
      Expr[] bound = new Expr[4];
      bound[0] = z3Ctx.mkConst("l1", attrListSort);
      bound[1] = z3Ctx.mkConst("l2", attrListSort);
      bound[2] = z3Ctx.mkConst("r1", relSort);
      bound[3] = z3Ctx.mkConst("r2", relSort);
      BoolExpr implyLhs = z3Ctx.mkEq(funcProj.apply(bound[0], bound[2]),
          funcProj.apply(bound[1], bound[3]));
      BoolExpr implyRhs = z3Ctx.mkEq(
          funcProj.apply(attrListSort.getConsDecl()
              .apply(attrListSort.getHeadDecl().apply(bound[0]), attrListSort.getNil()), bound[2]),
          funcProj.apply(attrListSort.getConsDecl()
              .apply(attrListSort.getHeadDecl().apply(bound[1]), attrListSort.getNil()), bound[3]));
      BoolExpr body = z3Ctx.mkImplies(implyLhs, implyRhs);
      BoolExpr theorem = z3Ctx.mkForall(bound, body, 1, null, null, null, null);
      solver.add(theorem);
    }
  }

  private void loadTheoremSelJoinAssoc(Solver solver) {
    FuncDecl funcEquiJoin = getFunctionByName("equi-join");
    FuncDecl funcSelEqv = getFunctionByName("sel-eqv");
    FuncDecl funcWidth = getFunctionByName("width");
    FuncDecl funcWidthPlus = getFunctionByName("width-plus");
    // sel-eqv_join_assoc_l
    // equi-join(a1, a2, sel-eqv(a, v, r1), r2)
    // = sel-eqv(a, v, equi-join(a1, a2, r1, r2))
    {
      Expr[] bound = new Expr[6];
      bound[0] = z3Ctx.mkConst("a1", attrSort);
      bound[1] = z3Ctx.mkConst("a2", attrSort);
      bound[2] = z3Ctx.mkConst("a", attrSort);
      bound[3] = z3Ctx.mkConst("v", valSort);
      bound[4] = z3Ctx.mkConst("r1", relSort);
      bound[5] = z3Ctx.mkConst("r2", relSort);
      Expr eqLhs = funcEquiJoin.apply(bound[0], bound[1],
          funcSelEqv.apply(bound[2], bound[3], bound[4]), bound[5]);
      Expr eqRhs = funcSelEqv.apply(bound[2], bound[3],
          funcEquiJoin.apply(bound[0], bound[1], bound[4], bound[5]));
      BoolExpr body = z3Ctx.mkEq(eqLhs, eqRhs);
      Pattern[] patterns = new Pattern[] { z3Ctx.mkPattern(eqLhs) };
      BoolExpr theorem = z3Ctx.mkForall(bound, body, 1, patterns, null, null, null);
      solver.add(theorem);
    }
    // sel-eqv_join_assoc_r
    // equi-join(a1, a2, r1, sel-eqv(a, v, r2))
    // = sel-eqv(width(r1) + a, v, equi-join(a1, a2, r1, r2))
    {
      Expr[] bound = new Expr[6];
      bound[0] = z3Ctx.mkConst("a1", attrSort);
      bound[1] = z3Ctx.mkConst("a2", attrSort);
      bound[2] = z3Ctx.mkConst("r1", relSort);
      bound[3] = z3Ctx.mkConst("a", attrSort);
      bound[4] = z3Ctx.mkConst("v", valSort);
      bound[5] = z3Ctx.mkConst("r2", relSort);
      Expr eqLhs = funcEquiJoin.apply(bound[0], bound[1], bound[2],
          funcSelEqv.apply(bound[3], bound[4], bound[5]));
      Expr eqRhs = funcSelEqv.apply(funcWidthPlus.apply(funcWidth.apply(bound[2]), bound[3]),
          bound[4], funcEquiJoin.apply(bound[0], bound[1], bound[2], bound[5]));
      BoolExpr body = z3Ctx.mkEq(eqLhs, eqRhs);
      Pattern[] patterns = new Pattern[] { z3Ctx.mkPattern(eqLhs) };
      BoolExpr theorem = z3Ctx.mkForall(bound, body, 1, patterns, null, null, null);
      solver.add(theorem);
    }
  }

  private void loadTheoremSameIndexGen(Solver solver) {
    FuncDecl funcListPair = getFunctionByName("list-pair");
    FuncDecl funcProj = getFunctionByName("proj");
    FuncDecl funcSameIndex = getFunctionByName("same-index");
    // list-pair_gen
    // proj(l1, r1) = proj(l2, r2) => list-pair(l1, l2, l1, l2)
    {
      Expr[] bound = new Expr[4];
      bound[0] = z3Ctx.mkConst("l1", attrListSort);
      bound[1] = z3Ctx.mkConst("r1", relSort);
      bound[2] = z3Ctx.mkConst("l2", attrListSort);
      bound[3] = z3Ctx.mkConst("r2", relSort);
      BoolExpr premise = z3Ctx.mkEq(funcProj.apply(bound[0], bound[1]),
          funcProj.apply(bound[2], bound[3]));
      BoolExpr concl = (BoolExpr) funcListPair.apply(bound[0], bound[2], bound[0], bound[2]);
      BoolExpr body = z3Ctx.mkImplies(premise, concl);
      BoolExpr theorem = z3Ctx.mkForall(bound, body, 1, null, null, null, null);
      solver.add(theorem);
    }
    // same-index_gen
    // list-pair(l1, l2, l3, l4) =>
    // same-index(head(l1), head(l2), l3, l4) /\ list-pair(tail(l1), tail(l2), l3, l4)
    {
      Expr[] bound = new Expr[4];
      bound[0] = z3Ctx.mkConst("l1", attrListSort);
      bound[1] = z3Ctx.mkConst("l2", attrListSort);
      bound[2] = z3Ctx.mkConst("l3", attrListSort);
      bound[3] = z3Ctx.mkConst("l4", attrListSort);
      BoolExpr premise = (BoolExpr) funcListPair.apply(bound[0], bound[1], bound[2], bound[3]);
      BoolExpr concl1 = (BoolExpr) funcSameIndex.apply(attrListSort.getHeadDecl().apply(bound[0]),
          attrListSort.getHeadDecl().apply(bound[1]), bound[2], bound[3]);
      BoolExpr concl2 = (BoolExpr) funcListPair.apply(attrListSort.getTailDecl().apply(bound[0]),
          attrListSort.getTailDecl().apply(bound[1]), bound[2], bound[3]);
      BoolExpr body = z3Ctx.mkImplies(premise, z3Ctx.mkAnd(concl1, concl2));
      Pattern[] patterns = new Pattern[] { z3Ctx.mkPattern(premise) };
      BoolExpr theorem = z3Ctx.mkForall(bound, body, 1, patterns, null, null, null);
      solver.add(theorem);
    }
  }

  private void loadTheoremProjUpdElim(Solver solver) {
    FuncDecl funcMemList = getFunctionByName("mem-list");
    FuncDecl funcProj = getFunctionByName("proj");
    FuncDecl funcUpd = getFunctionByName("upd");
    // upd_elim
    // mem-list(a, l) = false => proj(l, upd(r, a, v)) = proj(l, r)
    {
      Expr[] bound = new Expr[4];
      bound[0] = z3Ctx.mkConst("l", attrListSort);
      bound[1] = z3Ctx.mkConst("r", relSort);
      bound[2] = z3Ctx.mkConst("a", attrSort);
      bound[3] = z3Ctx.mkConst("v", valSort);
      BoolExpr premise = z3Ctx.mkEq(z3Ctx.mkFalse(), funcMemList.apply(bound[2], bound[0]));
      Expr eqLhs = funcProj.apply(bound[0], funcUpd.apply(bound[1], bound[2], bound[3]));
      Expr eqRhs = funcProj.apply(bound[0], bound[1]);
      BoolExpr body = z3Ctx.mkImplies(premise, z3Ctx.mkEq(eqLhs, eqRhs));
      Pattern[] patterns = new Pattern[] { z3Ctx.mkPattern(eqLhs) };
      BoolExpr theorem = z3Ctx.mkForall(bound, body, 1, patterns, null, null, null);
      solver.add(theorem);
    }
  }

  private void loadTheoremUpdJoinComm(Solver solver) {
    FuncDecl funcEquiJoin = getFunctionByName("equi-join");
    FuncDecl funcUpd = getFunctionByName("upd");
    FuncDecl funcWidth = getFunctionByName("width");
    FuncDecl funcWidthPlus = getFunctionByName("width-plus");
    // upd_join_comm_l
    // not(a1 = a) => join(a1, a2, upd(r1, a, v), r2) = upd(join(a1, a2, r1, r2), a, v)
    {
      Expr[] bound = new Expr[6];
      bound[0] = z3Ctx.mkConst("a1", attrSort);
      bound[1] = z3Ctx.mkConst("a2", attrSort);
      bound[2] = z3Ctx.mkConst("r1", relSort);
      bound[3] = z3Ctx.mkConst("a", attrSort);
      bound[4] = z3Ctx.mkConst("v", valSort);
      bound[5] = z3Ctx.mkConst("r2", relSort);
      BoolExpr premise = z3Ctx.mkNot(z3Ctx.mkEq(bound[0], bound[3]));
      Expr eqLhs = funcEquiJoin.apply(bound[0], bound[1],
          funcUpd.apply(bound[2], bound[3], bound[4]), bound[5]);
      Expr eqRhs = funcUpd.apply(funcEquiJoin.apply(bound[0], bound[1], bound[2], bound[5]),
          bound[3], bound[4]);
      BoolExpr body = z3Ctx.mkImplies(premise, z3Ctx.mkEq(eqLhs, eqRhs));
      Pattern[] patterns = new Pattern[] { z3Ctx.mkPattern(eqLhs) };
      BoolExpr theorem = z3Ctx.mkForall(bound, body, 1, patterns, null, null, null);
      solver.add(theorem);
    }
    // upd_join_comm_r
    // not(a2 = width(r1) + a)
    // => join(a1, a2, r1, upd(r2, a, v)) = upd(join(a1, a2, r1, r2), width(r1) + a, v)
    {
      Expr[] bound = new Expr[6];
      bound[0] = z3Ctx.mkConst("a1", attrSort);
      bound[1] = z3Ctx.mkConst("a2", attrSort);
      bound[2] = z3Ctx.mkConst("r1", relSort);
      bound[3] = z3Ctx.mkConst("r2", relSort);
      bound[4] = z3Ctx.mkConst("a", attrSort);
      bound[5] = z3Ctx.mkConst("v", valSort);
      Expr attr = funcWidthPlus.apply(funcWidth.apply(bound[2]), bound[4]);
      BoolExpr premise = z3Ctx.mkNot(z3Ctx.mkEq(bound[1], attr));
      Expr eqLhs = funcEquiJoin.apply(bound[0], bound[1], bound[2],
          funcUpd.apply(bound[3], bound[4], bound[5]));
      Expr eqRhs = funcUpd.apply(funcEquiJoin.apply(bound[0], bound[1], bound[2], bound[3]), attr,
          bound[5]);
      BoolExpr body = z3Ctx.mkImplies(premise, z3Ctx.mkEq(eqLhs, eqRhs));
      Pattern[] patterns = new Pattern[] { z3Ctx.mkPattern(eqLhs) };
      BoolExpr theorem = z3Ctx.mkForall(bound, body, 1, patterns, null, null, null);
      solver.add(theorem);
    }
  }

  private void loadTheoremSelAppDist(Solver solver) {
    FuncDecl funcSelEqv = getFunctionByName("sel-eqv");
    FuncDecl funcAppend = getFunctionByName("append");
    // sel-eqv_app_dist
    // sel-eqv(a, v, app(r1, r2)) = app(sel-eqv(a, v, r1), sel-eqv(a, v, r2))
    {
      Expr[] bound = new Expr[4];
      bound[0] = z3Ctx.mkConst("a", attrSort);
      bound[1] = z3Ctx.mkConst("v", valSort);
      bound[2] = z3Ctx.mkConst("r1", relSort);
      bound[3] = z3Ctx.mkConst("r2", relSort);
      Expr eqLhs = funcSelEqv.apply(bound[0], bound[1], funcAppend.apply(bound[2], bound[3]));
      Expr eqRhs = funcAppend.apply(funcSelEqv.apply(bound[0], bound[1], bound[2]),
          funcSelEqv.apply(bound[0], bound[1], bound[3]));
      BoolExpr body = z3Ctx.mkEq(eqLhs, eqRhs);
      Pattern[] patterns = new Pattern[] { z3Ctx.mkPattern(eqLhs) };
      BoolExpr theorem = z3Ctx.mkForall(bound, body, Options.SOLVER_QI_LARGE_WEIGHT, patterns, null,
          null, null);
      solver.add(theorem);
    }
  }

  private void loadTheoremSelMinusDist(Solver solver) {
    FuncDecl funcSelEqv = getFunctionByName("sel-eqv");
    FuncDecl funcMinus = getFunctionByName("minus");
    // sel-eqv_minus_dist
    // sel-eqv(a, v, minus(r1, r2)) = minus(sel-eqv(a, v, r1), sel-eqv(a, v, r2))
    {
      Expr[] bound = new Expr[4];
      bound[0] = z3Ctx.mkConst("a", attrSort);
      bound[1] = z3Ctx.mkConst("v", valSort);
      bound[2] = z3Ctx.mkConst("r1", relSort);
      bound[3] = z3Ctx.mkConst("r2", relSort);
      Expr eqLhs = funcSelEqv.apply(bound[0], bound[1], funcMinus.apply(bound[2], bound[3]));
      Expr eqRhs = funcMinus.apply(funcSelEqv.apply(bound[0], bound[1], bound[2]),
          funcSelEqv.apply(bound[0], bound[1], bound[3]));
      BoolExpr body = z3Ctx.mkEq(eqLhs, eqRhs);
      Pattern[] patterns = new Pattern[] { z3Ctx.mkPattern(eqLhs) };
      BoolExpr theorem = z3Ctx.mkForall(bound, body, 1, patterns, null, null, null);
      solver.add(theorem);
    }
  }

  private void loadTheoremSelUpdSimpl(Solver solver) {
    FuncDecl funcSelEqv = getFunctionByName("sel-eqv");
    FuncDecl funcUpd = getFunctionByName("upd");
    // sel_upd_sel_simpl
    // not(a1 = a2) =>
    // sel-eqv(a1, v1, upd(sel-eqv(a1, v1, r), a2, v2)) = upd(sel-eqv(a1, v1, r), a2, v2)
    {
      Expr[] bound = new Expr[5];
      bound[0] = z3Ctx.mkConst("a1", attrSort);
      bound[1] = z3Ctx.mkConst("v1", valSort);
      bound[2] = z3Ctx.mkConst("r", relSort);
      bound[3] = z3Ctx.mkConst("a2", attrSort);
      bound[4] = z3Ctx.mkConst("v2", valSort);
      BoolExpr premise = z3Ctx.mkNot(z3Ctx.mkEq(bound[0], bound[3]));
      Expr eqLhs = funcSelEqv.apply(bound[0], bound[1],
          funcUpd.apply(funcSelEqv.apply(bound[0], bound[1], bound[2]), bound[3], bound[4]));
      Expr eqRhs = funcUpd.apply(funcSelEqv.apply(bound[0], bound[1], bound[2]), bound[3],
          bound[4]);
      BoolExpr body = z3Ctx.mkImplies(premise, z3Ctx.mkEq(eqLhs, eqRhs));
      Pattern[] patterns = new Pattern[] { z3Ctx.mkPattern(eqLhs) };
      BoolExpr theorem = z3Ctx.mkForall(bound, body, 1, patterns, null, null, null);
      solver.add(theorem);
    }
  }

  private void loadTheoremUpdComm(Solver solver) {
    FuncDecl funcUpd = getFunctionByName("upd");
    // upd_comm
    // not(a1 = a2) =>
    // upd(upd(r, a1, v1), a2, v2) = upd(upd(r, a2, v2), a1, v1)
    {
      Expr[] bound = new Expr[5];
      bound[0] = z3Ctx.mkConst("r", relSort);
      bound[1] = z3Ctx.mkConst("a1", attrSort);
      bound[2] = z3Ctx.mkConst("v1", valSort);
      bound[3] = z3Ctx.mkConst("a2", attrSort);
      bound[4] = z3Ctx.mkConst("v2", valSort);
      BoolExpr premise = z3Ctx.mkNot(z3Ctx.mkEq(bound[1], bound[3]));
      Expr eqLhs = funcUpd.apply(funcUpd.apply(bound[0], bound[1], bound[2]), bound[3], bound[4]);
      Expr eqRhs = funcUpd.apply(funcUpd.apply(bound[0], bound[3], bound[4]), bound[1], bound[2]);
      BoolExpr body = z3Ctx.mkImplies(premise, z3Ctx.mkEq(eqLhs, eqRhs));
      Pattern[] patterns = new Pattern[] { z3Ctx.mkPattern(eqLhs) };
      BoolExpr theorem = z3Ctx.mkForall(bound, body, 1, patterns, null, null, null);
      solver.add(theorem);
    }
  }

  private void loadTheoremWidthPlus(Solver solver) {
    FuncDecl funcWidthPlus = getFunctionByName("width-plus");
    // width-plus_def
    {
      Expr[] bound = new Expr[2];
      bound[0] = z3Ctx.mkConst("a1", attrSort);
      bound[1] = z3Ctx.mkConst("a2", attrSort);
      Expr eqLhs = funcWidthPlus.apply(bound[0], bound[1]);
      Expr eqRhs = z3Ctx.mkAdd((ArithExpr) bound[0], (ArithExpr) bound[1]);
      BoolExpr body = z3Ctx.mkEq(eqLhs, eqRhs);
      Pattern[] patterns = new Pattern[] { z3Ctx.mkPattern(eqLhs) };
      BoolExpr theorem = z3Ctx.mkForall(bound, body, 1, patterns, null, null, null);
      solver.add(theorem);
    }
  }

  private void loadTheoremSelJoinPkfk(Solver solver) {
    FuncDecl funcEquiJoin = getFunctionByName("equi-join");
    FuncDecl funcSelEqv = getFunctionByName("sel-eqv");
    // sel-eqv_join_pkfk
    {
      Expr[] bound = new Expr[5];
      bound[0] = z3Ctx.mkConst("a1", attrSort);
      bound[1] = z3Ctx.mkConst("a2", attrSort);
      bound[2] = z3Ctx.mkConst("v", valSort);
      bound[3] = z3Ctx.mkConst("r1", relSort);
      bound[4] = z3Ctx.mkConst("r2", relSort);
      Expr eqLhs = funcSelEqv.apply(bound[0], bound[2],
          funcEquiJoin.apply(bound[0], bound[1], bound[3], bound[4]));
      Expr eqRhs = funcSelEqv.apply(bound[1], bound[2],
          funcEquiJoin.apply(bound[0], bound[1], bound[3], bound[4]));
      BoolExpr body = z3Ctx.mkEq(eqLhs, eqRhs);
      Pattern[] patterns = new Pattern[] { z3Ctx.mkPattern(eqLhs) };
      BoolExpr theorem = z3Ctx.mkForall(bound, body, 1, patterns, null, null, null);
      solver.add(theorem);
    }
  }

  private void loadSymbolicTheoremSameIndex(Solver solver) {
    FuncDecl funcSameIndex = getFunctionByName("same-index");
    FuncDecl funcProj = getFunctionByName("proj");
    FuncDecl funcSelEqv = getFunctionByName("sel-eqv");
    FuncDecl funcUpd = getFunctionByName("upd");
    // proj_sel-eqv_intro
    // proj(l1, r1) = proj(l2, r2) /\ same-index(a1, a2, l1, l2)
    // => proj(l1, sel-eqv(a1, v, r1)) = proj(l2, sel-eqv(a2, v, r2))
    {
      Expr[] bound = new Expr[7];
      bound[0] = z3Ctx.mkConst("l1", attrListSort);
      bound[1] = z3Ctx.mkConst("l2", attrListSort);
      bound[2] = z3Ctx.mkConst("r1", relSort);
      bound[3] = z3Ctx.mkConst("r2", relSort);
      bound[4] = z3Ctx.mkConst("a1", attrSort);
      bound[5] = z3Ctx.mkConst("a2", attrSort);
      bound[6] = z3Ctx.mkConst("v", valSort);
      BoolExpr premise = z3Ctx.mkAnd(
          z3Ctx.mkEq(funcProj.apply(bound[0], bound[2]), funcProj.apply(bound[1], bound[3])),
          (BoolExpr) funcSameIndex.apply(bound[4], bound[5], bound[0], bound[1]));
      BoolExpr conclusion = z3Ctx.mkEq(
          funcProj.apply(bound[0], funcSelEqv.apply(bound[4], bound[6], bound[2])),
          funcProj.apply(bound[1], funcSelEqv.apply(bound[5], bound[6], bound[3])));
      BoolExpr body = z3Ctx.mkImplies(premise, conclusion);
      BoolExpr theorem = z3Ctx.mkForall(bound, body, 1, null, null, null, null);
      solver.add(theorem);
    }
    // proj_upd_intro
    // proj(l1, r1) = proj(l2, r2) /\ same-index(a1, a2, l1, l2)
    // => proj(l1, upd(r1, a1, v)) = proj(l2, upd(r2, a2, v))
    {
      Expr[] bound = new Expr[7];
      bound[0] = z3Ctx.mkConst("l1", attrListSort);
      bound[1] = z3Ctx.mkConst("l2", attrListSort);
      bound[2] = z3Ctx.mkConst("r1", relSort);
      bound[3] = z3Ctx.mkConst("r2", relSort);
      bound[4] = z3Ctx.mkConst("a1", attrSort);
      bound[5] = z3Ctx.mkConst("a2", attrSort);
      bound[6] = z3Ctx.mkConst("v", valSort);
      BoolExpr premise = z3Ctx.mkAnd(
          z3Ctx.mkEq(funcProj.apply(bound[0], bound[2]), funcProj.apply(bound[1], bound[3])),
          (BoolExpr) funcSameIndex.apply(bound[4], bound[5], bound[0], bound[1]));
      BoolExpr conclusion = z3Ctx.mkEq(
          funcProj.apply(bound[0], funcUpd.apply(bound[2], bound[4], bound[6])),
          funcProj.apply(bound[1], funcUpd.apply(bound[3], bound[5], bound[6])));
      BoolExpr body = z3Ctx.mkImplies(premise, conclusion);
      BoolExpr theorem = z3Ctx.mkForall(bound, body, 1, null, null, null, null);
      solver.add(theorem);
    }
  }

}
