package edu.utexas.mediator.core;

import edu.utexas.mediator.ast.Program;

public class PerfectRefVerifier extends RefVerifier {

  public int benchId;

  public PerfectRefVerifier(int id) {
    super();
    benchId = id;
  }

  @Override
  public boolean verify(Program src, Program tgt) {
    ISynthesizer synthesizer = new PerfectSynthesizer(benchId);
    return verify(src, tgt, synthesizer);
  }

}
