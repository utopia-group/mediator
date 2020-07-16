package edu.utexas.mediator;

import java.util.logging.Logger;

import com.google.gson.Gson;

import edu.utexas.mediator.ast.Program;
import edu.utexas.mediator.bench.Benchmark;
import edu.utexas.mediator.core.EqvVerifier;
import edu.utexas.mediator.core.IVerifier;
import edu.utexas.mediator.core.IdentifierChecker;
import edu.utexas.mediator.core.ProgramRenamer;
import edu.utexas.mediator.parser.AntlrParser;
import edu.utexas.mediator.parser.IParser;

public class Mediator {

  public Mediator(Logger logger, int z3TimeoutMs) {
    LoggerWrapper.setInstance(logger);
    Options.SOLVER_TIMEOUT = z3TimeoutMs;
  }

  public boolean verify(String json) {
    Benchmark bench = new Gson().fromJson(json, Benchmark.class);
    IParser parser = new AntlrParser();
    Program source = parser.parse(bench.getSource());
    source.accept(new ProgramRenamer("_s"));
    source.accept(new IdentifierChecker());
    Program target = parser.parse(bench.getTarget());
    target.accept(new ProgramRenamer("_t"));
    target.accept(new IdentifierChecker());
    IVerifier verifier = new EqvVerifier();
    return verifier.verify(source, target);
  }

}
