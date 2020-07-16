package edu.utexas.mediator.constraint;

import java.util.ArrayList;
import java.util.List;

public class LConjunctList extends Constraint {

  protected List<Constraint> conjuncts;

  protected LConjunctList() {
    conjuncts = new ArrayList<>();
  }

  public List<Constraint> getConjuncts() {
    return conjuncts;
  }

  public void removeConjunct(Constraint cstr) {
    boolean removed = conjuncts.remove(cstr);
    assert removed : "Does not find constraint: " + cstr;
  }

  public int getConjunctNum() {
    return conjuncts.size();
  }

  public void addConstraint(Constraint cstr) {
    conjuncts.add(cstr);
  }

  public void merge(LConjunctList list) {
    conjuncts.addAll(list.conjuncts);
  }

  @Override
  public String toString() {
    return "conj" + conjuncts;
  }

  @Override
  public int hashCode() {
    return conjuncts.hashCode();
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) return true;
    if (o instanceof LConjunctList) {
      return conjuncts.equals(((LConjunctList) o).conjuncts);
    }
    return false;
  }

  @Override
  public LConjunctList substitute(LTerm from, LTerm to) {
    LConjunctList results = new LConjunctList();
    conjuncts.forEach((elem) -> results.addConstraint(elem.substitute(from, to)));
    return results;
  }

}
