package edu.utexas.mediator.constraint;

import java.util.LinkedHashMap;

public class LRelNil extends LTerm {

  public LRelNil() {
    // should specify the schema if used in the arithmetic
    // otherwise lead to wrong attribute indices computation
    // thus can only be used for denoting NIL
    super(new LinkedHashMap<>());
  }

  @Override
  public String toString() {
    return "r_nil";
  }

  @Override
  public int hashCode() {
    return 1;
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) return true;
    return (o instanceof LRelNil);
  }

  @Override
  public Constraint substitute(LTerm from, LTerm to) {
    return this;
  }

}
