package edu.utexas.mediator.ast;

public class DeleteNode extends AstNode {

  private RelationNode relation;
  private Predicate pred;

  public DeleteNode(RelationNode relation, Predicate pred) {
    this.relation = relation;
    this.pred = pred;
  }

  public RelationNode getRelation() {
    return relation;
  }

  public void setRelation(RelationNode relation) {
    this.relation = relation;
  }

  public Predicate getPred() {
    return pred;
  }

  public void setPred(Predicate pred) {
    this.pred = pred;
  }

  @Override
  public String toString() {
    return "del(" + relation + "," + pred + ")";
  }

  @Override
  public int hashCode() {
    return (7 * relation.hashCode()) ^ (13 * pred.hashCode());
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) return true;
    if (o instanceof DeleteNode) {
      return relation.equals(((DeleteNode) o).relation) && pred.equals(((DeleteNode) o).pred);
    }
    return false;
  }

  @Override
  public void accept(IVisitor visitor) {
    visitor.visit(this);
  }

}
