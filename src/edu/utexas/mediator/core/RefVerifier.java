package edu.utexas.mediator.core;

import java.util.List;

import edu.utexas.mediator.Enumerations.TranType;
import edu.utexas.mediator.Options;
import edu.utexas.mediator.ast.AstNode;
import edu.utexas.mediator.ast.Program;
import edu.utexas.mediator.ast.ProjectNode;
import edu.utexas.mediator.ast.Transaction;
import edu.utexas.mediator.constraint.Constraint;
import edu.utexas.mediator.constraint.Invariant;
import edu.utexas.mediator.constraint.LAttrList;
import edu.utexas.mediator.constraint.LConjunctList;
import edu.utexas.mediator.constraint.LEqTerm;
import edu.utexas.mediator.constraint.LImply;
import edu.utexas.mediator.constraint.LProject;
import edu.utexas.mediator.solver.UcqSolver;

public class RefVerifier extends EqvVerifier {

  @Override
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
      // optimization - first check equivalence and then check refinement
      AstNode srcQuery = srcTran.getStatements().get(0);
      AstNode tgtQuery = tgtTran.getStatements().get(0);
      assert srcQuery instanceof ProjectNode && tgtQuery instanceof ProjectNode;
      int srcAttrNum = ((ProjectNode) srcQuery).getAttrList().getAttributes().size();
      int tgtAttrNum = ((ProjectNode) tgtQuery).getAttrList().getAttributes().size();
      if (srcAttrNum == tgtAttrNum && checkValidityWithUcqAndZ3(vc)) continue;
      if (!verifyRefinement(vc)) return false;
    }
    return true;
  }

  private boolean verifyRefinement(Constraint cstr) {
    List<Constraint> vcs = new Permutator().veriCondPerm(cstr);
    for (Constraint vc : vcs) {
      ISolver solver = new UcqSolver();
      ++queryCount;
      boolean res = solver.isValid(vc);
      System.out.print(" ucq: " + res);
      if (!res) {
        solver = new Z3ListSolver(Options.SOLVER_QUERY_Z3_TIMEOUT);
        ++queryCount;
        res = solver.isValid(vc);
        System.out.print(" z3: " + res);
      }
      LAttrList attrs = ((LProject) ((LEqTerm) ((LImply) vc).getRhs()).getRhs()).getAttrList();
      LOGGER.fine(" " + attrs);
      if (res) return true;
    }
    return false;
  }

}
