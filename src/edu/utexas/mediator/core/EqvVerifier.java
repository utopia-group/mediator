package edu.utexas.mediator.core;

import java.util.List;
import java.util.logging.Logger;

import edu.utexas.mediator.Enumerations.TranType;
import edu.utexas.mediator.LoggerWrapper;
import edu.utexas.mediator.Options;
import edu.utexas.mediator.ast.AstNode;
import edu.utexas.mediator.ast.AstTerm;
import edu.utexas.mediator.ast.DeleteNode;
import edu.utexas.mediator.ast.InsertNode;
import edu.utexas.mediator.ast.Program;
import edu.utexas.mediator.ast.RelDecl;
import edu.utexas.mediator.ast.Transaction;
import edu.utexas.mediator.ast.UpdateNode;
import edu.utexas.mediator.constraint.Constraint;
import edu.utexas.mediator.constraint.Context;
import edu.utexas.mediator.constraint.Invariant;
import edu.utexas.mediator.constraint.LConjunctList;
import edu.utexas.mediator.constraint.LTerm;
import edu.utexas.mediator.solver.UcqSolver;

public class EqvVerifier implements IVerifier {

  protected static final Logger LOGGER = LoggerWrapper.getInstance();

  private Context ctx;

  protected int queryCount = 0;

  public EqvVerifier() {
    ctx = new Context();
  }

  @Override
  public boolean verify(Program src, Program tgt) {
    ISynthesizer synthesizer = new PrunedSynthesizer();
    return verify(src, tgt, synthesizer);
  }

  public boolean verify(Program src, Program tgt, ISynthesizer synthesizer) {
    if (Options.VERIFIER_ROLLING) {
      return verifyRolling(src, tgt, synthesizer);
    } else {
      return verifySequential(src, tgt, synthesizer);
    }
  }

  public boolean verifyRolling(Program src, Program tgt, ISynthesizer synthesizer) {
    Invariant inv = synthesizer.synthesize(src, tgt);
    LOGGER.fine("inv: " + inv);
    int iter = 0;
    LOGGER.fine("--- Iteration: " + (++iter) + " ---");
    while (checkSufficiency(inv, src, tgt)) {
      boolean ind = true;
      for (int i = 0; i < inv.getConjunctNum(); ++i) {
        Constraint cstr = inv.getConjuncts().get(i);
        if (Options.VERIFIER_VERBOSE) LOGGER.fine("- " + cstr + " -");
        if (!checkInductiveness(inv, src, tgt, cstr)) {
          if (Options.VERIFIER_VERBOSE) LOGGER.fine("- Removed!");
          inv = removeAndRollInvarint(inv, i);
          ind = false;
          break;
        }
      }
      if (ind) {
        if (Options.REPORT_STATISTICS) {
          LOGGER.fine("Inductive invariant: " + inv);
          LOGGER.fine("Iters: " + iter);
          LOGGER.fine("Queries: " + queryCount);
        }
        return true;
      }
      LOGGER.fine("--- Iteration: " + (++iter) + " ---");
    }
    return false;
  }

  private Invariant removeAndRollInvarint(Invariant inv, int index) {
    Invariant ret = ctx.mkInvariant();
    List<Constraint> cstrs = inv.getConjuncts();
    assert index < cstrs.size();
    for (int i = index + 1; i < cstrs.size(); ++i) {
      ret.addConstraint(cstrs.get(i));
    }
    for (int i = 0; i < index; ++i) {
      ret.addConstraint(cstrs.get(i));
    }
    return ret;
  }

  public boolean verifySequential(Program src, Program tgt, ISynthesizer synthesizer) {
    Invariant inv = synthesizer.synthesize(src, tgt);
    LOGGER.fine("inv: " + inv);
    int iter = 0;
    LOGGER.fine("--- Iteration: " + (++iter) + " ---");
    while (checkSufficiency(inv, src, tgt)) {
      boolean ind = true;
      for (Constraint cstr : inv.getConjuncts()) {
        if (Options.VERIFIER_VERBOSE) LOGGER.fine("- " + cstr + " -");
        if (!checkInductiveness(inv, src, tgt, cstr)) {
          if (Options.VERIFIER_VERBOSE) LOGGER.fine("- Removed!");
          inv.removeConjunct(cstr);
          ind = false;
          break;
        }
      }
      if (ind) {
        if (Options.REPORT_STATISTICS) {
          LOGGER.fine("Inductive invariant: " + inv);
          LOGGER.fine("Iters: " + iter);
          LOGGER.fine("Queries: " + queryCount);
        }
        return true;
      }
      LOGGER.fine("--- Iteration: " + (++iter) + " ---");
    }
    return false;
  }

  public boolean checkSufficiency(Invariant inv, Program src, Program tgt) {
    assert tgt.getTransactions().size() >= src.getTransactions().size();
    LConjunctList premise = generateWidth(src);
    premise.merge(generateWidth(tgt));
    int len = src.getTransactions().size();
    for (int i = 0; i < len; ++i) {
      Transaction srcTran = src.getTransactions().get(i);
      Transaction tgtTran = tgt.getTransactions().get(i);
      String tranName = srcTran.getSignature().getName();
      assert tranName.equals(tgtTran.getSignature().getName());
      TranType tranType = getTranType(tgtTran);
      assert tranType == getTranType(tgtTran);
      // skip the transactions that are not queries
      if (tranType != TranType.QUERY) continue;
      Constraint vc = genPreservationVC(inv, srcTran, tgtTran);
      LOGGER.fine(tranName);
      if (!checkValidityWithUcqAndZ3(vc)) return false;
    }
    return true;
  }

  public boolean checkInductiveness(Invariant inv, Program src, Program tgt, Constraint eq) {
    assert tgt.getTransactions().size() >= src.getTransactions().size();
    LConjunctList premise = generateWidth(src);
    premise.merge(generateWidth(tgt));
    int len = src.getTransactions().size();
    for (int i = 0; i < len; ++i) {
      Transaction srcTran = src.getTransactions().get(i);
      Transaction tgtTran = tgt.getTransactions().get(i);
      String tranName = srcTran.getSignature().getName();
      assert tranName.equals(tgtTran.getSignature().getName());
      TranType tranType = getTranType(tgtTran);
      assert tranType == getTranType(tgtTran);
      Constraint vc = null;
      if (tranType == TranType.INSERTION) {
        vc = genInsertionIndVC(inv, srcTran, tgtTran, eq);
      } else if (tranType == TranType.DELETION) {
        vc = genInductivenessVC(premise, inv, srcTran, tgtTran, eq);
      } else if (tranType == TranType.UPDATE) {
        vc = genInductivenessVC(premise, inv, srcTran, tgtTran, eq);
      } else {
        // skip the transactions that are not updates
        continue;
      }
      LOGGER.fine(tranName);
      if (!checkValidityWithZ3(vc)) return false;
    }
    return true;
  }

  protected Constraint genInductivenessVC(Constraint premise, Invariant inv, Transaction src,
      Transaction tgt, Constraint eq) {
    SymbolicExecutor executor = new SymbolicExecutor(ctx);
    Constraint sp = executor.execute(inv, src);
    sp = executor.execute(sp, tgt);
    return ctx.mkImply(ctx.mkAnd(premise, sp), eq);
  }

  protected Constraint genInsertionIndVC(Invariant inv, Transaction src, Transaction tgt,
      Constraint eq) {
    // FIXME - prove it is correct
    SymbolicExecutor executor = new SymbolicExecutor(ctx);
    Constraint conc = executor.execute(eq, src);
    conc = executor.execute(conc, tgt);
    return ctx.mkImply(inv, conc);
  }

  protected Constraint genPreservationVC(Invariant inv, Transaction src, Transaction tgt) {
    assert src.getStatements().size() == 1;
    assert tgt.getStatements().size() == 1;
    SymbolicExecutor executor = new SymbolicExecutor(ctx);
    AstNode srcQuery = src.getStatements().get(0);
    assert srcQuery instanceof AstTerm;
    LTerm lhs = executor.execute((AstTerm) srcQuery);
    AstNode tgtQuery = tgt.getStatements().get(0);
    assert tgtQuery instanceof AstTerm;
    LTerm rhs = executor.execute((AstTerm) tgtQuery);
    return ctx.mkImply(inv, ctx.mkEqTerm(lhs, rhs));
  }

  protected LConjunctList generateWidth(Program prog) {
    LConjunctList conjList = ctx.mkConjunctList();
    List<RelDecl> relDecls = prog.getSchema().getRelDecls();
    for (RelDecl relDecl : relDecls) {
      conjList.addConstraint(ctx.mkWidth(relDecl.getName(), relDecl.getAttrNum()));
    }
    return conjList;
  }

  protected boolean checkValidityWithUcqAndZ3(Constraint cstr) {
    ++queryCount;
    ISolver solver = new UcqSolver();
    boolean ucqResult = solver.isValid(cstr);
    LOGGER.fine(" ucq: " + ucqResult);
    if (ucqResult) {
      return true;
    }
    --queryCount;
    return checkValidityWithZ3(cstr);
  }

  protected boolean checkValidityWithZ3(Constraint cstr) {
    ++queryCount;
    ISolver solver = new Z3ListSolver();
    boolean z3Result = solver.isValid(cstr);
    LOGGER.fine(" z3: " + z3Result);
    return z3Result;
  }

  protected TranType getTranType(Transaction tran) {
    assert tran.getStatements().size() > 0;
    // infer transaction type by the first statement
    AstNode stmt = tran.getStatements().get(0);
    if (stmt instanceof AstTerm) {
      return TranType.QUERY;
    } else if (stmt instanceof InsertNode) {
      return TranType.INSERTION;
    } else if (stmt instanceof DeleteNode) {
      return TranType.DELETION;
    } else if (stmt instanceof UpdateNode) {
      return TranType.UPDATE;
    } else {
      return TranType.UNKNOWN;
    }
  }

}
