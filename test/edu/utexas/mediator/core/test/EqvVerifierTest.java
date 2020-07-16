package edu.utexas.mediator.core.test;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;

import edu.utexas.mediator.ast.Program;
import edu.utexas.mediator.bench.Benchmark;
import edu.utexas.mediator.constraint.Context;
import edu.utexas.mediator.constraint.Invariant;
import edu.utexas.mediator.constraint.LAttrList;
import edu.utexas.mediator.constraint.LRelConst;
import edu.utexas.mediator.constraint.LTerm;
import edu.utexas.mediator.core.EqvVerifier;
import edu.utexas.mediator.core.ISynthesizer;
import edu.utexas.mediator.core.IdentifierChecker;
import edu.utexas.mediator.core.ProgramRenamer;
import edu.utexas.mediator.parser.AntlrParser;
import edu.utexas.mediator.parser.IParser;
import edu.utexas.mediator.util.FileUtil;

public class EqvVerifierTest {

  @Test
  public void testBenchmark() {
    int id = 7;
    String filename = "benchmarks/benchmark" + id + ".json";
    Benchmark bench = FileUtil.fromJson(filename, Benchmark.class);
    IParser parser = new AntlrParser();
    Program source = parser.parse(bench.getSource());
    source.accept(new ProgramRenamer("_s"));
    source.accept(new IdentifierChecker());
    Program target = parser.parse(bench.getTarget());
    target.accept(new ProgramRenamer("_t"));
    target.accept(new IdentifierChecker());
    EqvVerifier verifier = new EqvVerifier();
    ISynthesizer synthesizer = new MockSynthesizer();
    boolean res = verifier.verify(source, target, synthesizer);
    Assert.assertTrue(res);
  }

  private class MockSynthesizer implements ISynthesizer {

    private final String sSuf = "_s";
    private final String tSuf = "_t";

    @Override
    public Invariant synthesize(Program src, Program tgt) {
      Context ctx = new Context();
      LRelConst customerS = ctx.mkRelConst(src.getSchema().getRelDeclByName("Customer" + sSuf));
      LRelConst policyS = ctx.mkRelConst(src.getSchema().getRelDeclByName("Policy" + sSuf));
      LRelConst customerT = ctx.mkRelConst(tgt.getSchema().getRelDeclByName("Customer" + tSuf));
      LRelConst policyT = ctx.mkRelConst(tgt.getSchema().getRelDeclByName("Policy" + tSuf));
      LRelConst holdsT = ctx.mkRelConst(tgt.getSchema().getRelDeclByName("Holds" + tSuf));
      Invariant inv = ctx.mkInvariant();
      {
        String[] lhsAttrArray = new String[] { "CustomerPOID" + sSuf };
        LAttrList lhsAttrList = ctx.mkAttrList(new ArrayList<>(Arrays.asList(lhsAttrArray)));
        String[] rhsAttrArray = new String[] { "CustomerPOID" + tSuf };
        LAttrList rhsAttrList = ctx.mkAttrList(new ArrayList<>(Arrays.asList(rhsAttrArray)));
        LTerm lhs = ctx.mkProject(lhsAttrList, customerS);
        LTerm rhs = ctx.mkProject(rhsAttrList, customerT);
        inv.addConstraint(ctx.mkEqTerm(lhs, rhs));
      }
      {
        String[] lhsAttrArray = new String[] { "CustomerPOID" + sSuf, "Name" + sSuf };
        LAttrList lhsAttrList = ctx.mkAttrList(new ArrayList<>(Arrays.asList(lhsAttrArray)));
        String[] rhsAttrArray = new String[] { "CustomerPOID" + tSuf, "Name" + tSuf };
        LAttrList rhsAttrList = ctx.mkAttrList(new ArrayList<>(Arrays.asList(rhsAttrArray)));
        LTerm lhs = ctx.mkProject(lhsAttrList, customerS);
        LTerm rhs = ctx.mkProject(rhsAttrList, customerT);
        inv.addConstraint(ctx.mkEqTerm(lhs, rhs));
      }
      {
        String[] lhsAttrArray = new String[] { "PolicyID" + sSuf, "CustomerPOID_fk" + sSuf,
            "Amount" + sSuf };
        LAttrList lhsAttrList = ctx.mkAttrList(new ArrayList<>(Arrays.asList(lhsAttrArray)));
        String[] rhsAttrArray = new String[] { "CustomerPOID_fk" + tSuf, "PolicyID" + tSuf,
            "Amount" + tSuf };
        LAttrList rhsAttrList = ctx.mkAttrList(new ArrayList<>(Arrays.asList(rhsAttrArray)));
        LTerm lhs = ctx.mkProject(lhsAttrList, policyS);
        LTerm rhs = ctx.mkProject(rhsAttrList, ctx.mkEquiJoin(holdsT, policyT,
            ctx.mkAttrConst("PolicyID_fk" + tSuf), ctx.mkAttrConst("PolicyID" + tSuf)));
        inv.addConstraint(ctx.mkEqTerm(lhs, rhs));
      }
      {
        String[] lhsAttrArray = new String[] { "PolicyID" + sSuf, "CustomerPOID_fk" + sSuf,
            "Amount" + sSuf };
        LAttrList lhsAttrList = ctx.mkAttrList(new ArrayList<>(Arrays.asList(lhsAttrArray)));
        String[] rhsAttrArray = new String[] { "Amount" + tSuf, "CustomerPOID_fk" + tSuf,
            "PolicyID" + tSuf };
        LAttrList rhsAttrList = ctx.mkAttrList(new ArrayList<>(Arrays.asList(rhsAttrArray)));
        LTerm lhs = ctx.mkProject(lhsAttrList, policyS);
        LTerm rhs = ctx.mkProject(rhsAttrList, ctx.mkEquiJoin(holdsT, policyT,
            ctx.mkAttrConst("PolicyID_fk" + tSuf), ctx.mkAttrConst("PolicyID" + tSuf)));
        inv.addConstraint(ctx.mkEqTerm(lhs, rhs));
      }
      {
        String[] lhsAttrArray = new String[] { "PolicyID" + sSuf, "CustomerPOID_fk" + sSuf,
            "Amount" + sSuf };
        LAttrList lhsAttrList = ctx.mkAttrList(new ArrayList<>(Arrays.asList(lhsAttrArray)));
        String[] rhsAttrArray = new String[] { "PolicyID" + tSuf, "CustomerPOID_fk" + tSuf,
            "Amount" + tSuf };
        LAttrList rhsAttrList = ctx.mkAttrList(new ArrayList<>(Arrays.asList(rhsAttrArray)));
        LTerm lhs = ctx.mkProject(lhsAttrList, policyS);
        LTerm rhs = ctx.mkProject(rhsAttrList, ctx.mkEquiJoin(holdsT, policyT,
            ctx.mkAttrConst("PolicyID_fk" + tSuf), ctx.mkAttrConst("PolicyID" + tSuf)));
        inv.addConstraint(ctx.mkEqTerm(lhs, rhs));
      }
      return inv;
    }

  }

}
