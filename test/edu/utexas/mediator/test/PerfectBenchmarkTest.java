package edu.utexas.mediator.test;

import org.junit.Assert;
import org.junit.Test;

import edu.utexas.mediator.ast.Program;
import edu.utexas.mediator.bench.Benchmark;
import edu.utexas.mediator.core.IVerifier;
import edu.utexas.mediator.core.IdentifierChecker;
import edu.utexas.mediator.core.PerfectEqvVerifier;
import edu.utexas.mediator.core.PerfectRefVerifier;
import edu.utexas.mediator.core.ProgramRenamer;
import edu.utexas.mediator.parser.AntlrParser;
import edu.utexas.mediator.parser.IParser;
import edu.utexas.mediator.util.FileUtil;
import edu.utexas.mediator.util.StatUtil;

public class PerfectBenchmarkTest {

  @Test
  public void testBenchmark1() {
    Assert.assertTrue(runEqvVerifier(1));
  }

  @Test
  public void testBenchmark9() {
    Assert.assertTrue(runRefVerifier(9));
  }

  private boolean runEqvVerifier(int id) {
    IVerifier verifier = new PerfectEqvVerifier(id);
    return runVerifier(verifier, id);
  }

  private boolean runRefVerifier(int id) {
    IVerifier verifier = new PerfectRefVerifier(id);
    return runVerifier(verifier, id);
  }

  private boolean runVerifier(IVerifier verifier, int id) {
    String filename = "benchmarks/benchmark" + id + ".json";
    System.out.println("============ " + filename);
    Benchmark bench = FileUtil.fromJson(filename, Benchmark.class);
    IParser parser = new AntlrParser();
    Program source = parser.parse(bench.getSource());
    System.out.println("Source: " + StatUtil.getStatistics(source));
    source.accept(new ProgramRenamer("_s"));
    source.accept(new IdentifierChecker());
    Program target = parser.parse(bench.getTarget());
    System.out.println("Target: " + StatUtil.getStatistics(target));
    target.accept(new ProgramRenamer("_t"));
    target.accept(new IdentifierChecker());
    return verifier.verify(source, target);
  }

}
