package edu.utexas.mediator.constraint;

public class LExists extends Constraint {

  private final LRelVar variable;
  private final Constraint formula;

  protected LExists(LRelVar variable, Constraint formula) {
    this.variable = variable;
    this.formula = formula;
  }

  public LRelVar getVariable() {
    return variable;
  }

  public Constraint getFormula() {
    return formula;
  }

  @Override
  public String toString() {
    return "exists(" + variable + "," + formula + ")";
  }

  @Override
  public int hashCode() {
    return variable.hashCode() ^ formula.hashCode();
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) return true;
    if (o instanceof LExists) {
      return variable.equals(((LExists) o).variable) && formula.equals(((LExists) o).formula);
    }
    return false;
  }

  @Override
  public LExists substitute(LTerm from, LTerm to) {
    assert !variable.equals(from) && !variable.equals(to);
    return new LExists(variable, formula.substitute(from, to));
  }

}
