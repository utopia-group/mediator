package edu.utexas.mediator.solver;

public class Rule {
  private Graph src;
  private Graph tgt;

  public Rule(Graph src, Graph tgt) {
    this.src = src;
    this.tgt = tgt;
  }

  public Graph getSrcGraph() {
    return src;
  }

  public Graph getTgtGraph() {
    return tgt;
  }

  @Override
  public String toString() {
    return String.format("%s -> %s", src, tgt);
  }

  @Override
  public int hashCode() {
    return src.hashCode() ^ tgt.hashCode();
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) return true;
    if (o instanceof Rule) {
      return src.equals(((Rule) o).src) && tgt.equals(((Rule) o).tgt);
    }
    return false;
  }

}
