package edu.utexas.mediator.constraint;

import java.util.Set;

public class Invariant extends LConjunctList {

  protected Invariant() {
    super();
  }

  public Invariant retain(Set<String> rels) {
    Invariant inv = new Invariant();
    for (Constraint cstr : conjuncts) {
      if (containsRel(cstr, rels)) {
        inv.conjuncts.add(cstr);
      }
    }
    return inv;
  }

  private boolean containsRel(Constraint cstr, Set<String> rels) {
    assert cstr instanceof LEqTerm;
    return containsRel((LEqTerm) cstr, rels);
  }

  private boolean containsRel(LEqTerm eq, Set<String> rels) {
    assert eq.getLhs() instanceof LProject;
    assert eq.getRhs() instanceof LProject;
    return containsRel((LProject) eq.getLhs(), rels) || containsRel((LProject) eq.getRhs(), rels);
  }

  private boolean containsRel(LProject proj, Set<String> rels) {
    LTerm term = proj.getRelation();
    if (term instanceof LRelConst) {
      return containsRel((LRelConst) term, rels);
    } else if (term instanceof LEquiJoin) {
      return containsRel((LEquiJoin) term, rels);
    } else {
      throw new RuntimeException("Unsupported term type: " + term);
    }
  }

  private boolean containsRel(LEquiJoin join, Set<String> rels) {
    assert join.getLhs() instanceof LRelConst;
    assert join.getRhs() instanceof LRelConst;
    return containsRel((LRelConst) join.getLhs(), rels)
        || containsRel((LRelConst) join.getRhs(), rels);
  }

  private boolean containsRel(LRelConst rel, Set<String> rels) {
    return rels.contains(rel.getName());
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("conj").append(conjuncts.size()).append("[\n");
    conjuncts.forEach((conj) -> builder.append(conj).append("\n"));
    builder.append("]");
    return builder.toString();
  }

}
