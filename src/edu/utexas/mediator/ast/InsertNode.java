package edu.utexas.mediator.ast;

public class InsertNode extends AstNode {

  private RelationNode relation;
  private TupleNode tuple;

  public InsertNode(RelationNode relation, TupleNode tuple) {
    this.relation = relation;
    this.tuple = tuple;
  }

  public RelationNode getRelation() {
    return relation;
  }

  public void setRelation(RelationNode relation) {
    this.relation = relation;
  }

  public TupleNode getTuple() {
    return tuple;
  }

  public void setTuple(TupleNode tuple) {
    this.tuple = tuple;
  }

  @Override
  public String toString() {
    return "ins(" + relation + "," + tuple + ")";
  }

  @Override
  public int hashCode() {
    return (5 * relation.hashCode()) ^ (11 * tuple.hashCode());
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) return true;
    if (o instanceof InsertNode) {
      return relation.equals(((InsertNode) o).relation) && tuple.equals(((InsertNode) o).tuple);
    }
    return false;
  }

  @Override
  public void accept(IVisitor visitor) {
    visitor.visit(this);
  }

}
