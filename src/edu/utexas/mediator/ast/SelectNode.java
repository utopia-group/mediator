package edu.utexas.mediator.ast;

public class SelectNode extends AstTerm {

  private Predicate pred;
  private AstTerm relation;

  public SelectNode(Predicate pred, AstTerm relation) {
    this.pred = pred;
    this.relation = relation;
  }

  public Predicate getPred() {
    return pred;
  }

  public void setPred(Predicate pred) {
    this.pred = pred;
  }

  public AstTerm getRelation() {
    return relation;
  }

  public void setRelation(AstTerm relation) {
    this.relation = relation;
  }

  @Override
  public String toString() {
    return "sigma(" + pred + "," + relation + ")";
  }

  @Override
  public int hashCode() {
    return (23 * pred.hashCode()) ^ (29 * relation.hashCode());
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) return true;
    if (o instanceof SelectNode) {
      return pred.equals(((SelectNode) o).pred) && relation.equals(((SelectNode) o).relation);
    }
    return false;
  }

  @Override
  public void accept(IVisitor visitor) {
    visitor.visit(this);
  }

}
