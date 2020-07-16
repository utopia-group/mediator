package edu.utexas.mediator.ast;

public class UpdateNode extends AstNode {

  private RelationNode relation;
  private Predicate pred;
  private String attr;
  private String value;

  public UpdateNode(RelationNode relation, Predicate pred, String attr, String value) {
    this.relation = relation;
    this.pred = pred;
    this.attr = attr;
    this.value = value;
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

  public String getAttr() {
    return attr;
  }

  public void setAttr(String attr) {
    this.attr = attr;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  @Override
  public String toString() {
    return "upd(" + relation + "," + pred + "," + attr + "," + value + ")";
  }

  @Override
  public int hashCode() {
    return (11 * relation.hashCode()) ^ (19 * pred.hashCode()) ^ attr.hashCode() ^ value.hashCode();
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) return true;
    if (o instanceof UpdateNode) {
      return relation.equals(((UpdateNode) o).relation) && pred.equals(((UpdateNode) o).pred)
          && attr.equals(((UpdateNode) o).attr) && value.equals(((UpdateNode) o).attr);
    }
    return false;
  }

  @Override
  public void accept(IVisitor visitor) {
    visitor.visit(this);
  }

}
