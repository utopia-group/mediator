package edu.utexas.mediator;

public class Options {

  // timeout in milliseconds per query to Z3 list solver
  public static int SOLVER_TIMEOUT = 2000;

  // timeout in milliseconds for Z3 query solver in RefVerifier
  public static final int SOLVER_QUERY_Z3_TIMEOUT = 2000;

  // eager quantifier instantiation threshold
  public static final double SOLVER_QI_EAGER_THRES = 40;

  // weight for expensive quantifier instantiation
  public static final int SOLVER_QI_LARGE_WEIGHT = 5;

  // display internal formula
  public static final boolean PRINT_INTERNAL_FORMULA = false;

  // display Z3 formula and result
  public static final boolean PRINT_Z3_FORMULA = false;

  // enable theorems related to selection distributivity
  public static final boolean ENABLE_SEL_DIST = false;

  // UCQ solver verbose mode - display premises, source and target
  public static final boolean UCQ_SOLVER_VERBOSE = false;

  // Verifier verbose mode
  public static final boolean VERIFIER_VERBOSE = true;

  // Verifier rolling mode
  public static final boolean VERIFIER_ROLLING = true;

  // report statistics
  public static final boolean REPORT_STATISTICS = true;

}
