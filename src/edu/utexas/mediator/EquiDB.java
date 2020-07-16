package edu.utexas.mediator;

import java.util.logging.Logger;

import edu.utexas.mediator.ast.Program;
import edu.utexas.mediator.bench.Benchmark;
import edu.utexas.mediator.core.EqvVerifier;
import edu.utexas.mediator.core.IVerifier;
import edu.utexas.mediator.core.IdentifierChecker;
import edu.utexas.mediator.core.ProgramRenamer;
import edu.utexas.mediator.parser.AntlrParser;
import edu.utexas.mediator.parser.IParser;
import edu.utexas.mediator.util.FileUtil;
import edu.utexas.mediator.util.StatUtil;

public class EquiDB {

  private static final Logger LOGGER = LoggerWrapper.getInstance();

  public static void main(String[] args) {
    int id = 0;
    String filename = "benchmarks/benchmark" + id + ".json";
    Benchmark bench = FileUtil.fromJson(filename, Benchmark.class);
    IParser parser = new AntlrParser();
    Program source = parser.parse(bench.getSource());
    LOGGER.fine("Source: " + StatUtil.getStatistics(source));
    source.accept(new ProgramRenamer("_s"));
    source.accept(new IdentifierChecker());
    Program target = parser.parse(bench.getTarget());
    LOGGER.fine("Target: " + StatUtil.getStatistics(target));
    target.accept(new ProgramRenamer("_t"));
    target.accept(new IdentifierChecker());
    IVerifier verifier = new EqvVerifier();
    boolean result = verifier.verify(source, target);
    LOGGER.info("Eqv: " + result);
  }

}
