package edu.utexas.mediator.parser;

import edu.utexas.mediator.ast.Program;
import edu.utexas.mediator.bench.Prog;

public interface IParser {

  /**
   * Parse a program in json format to AST
   * 
   * @param prog
   *          program in json
   * @return program AST
   */
  public Program parse(Prog prog);

}
