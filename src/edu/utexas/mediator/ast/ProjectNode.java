package edu.utexas.mediator.ast;

public class ProjectNode extends AstTerm {

  private AttrListNode attrList;
  private AstTerm relation;

  public ProjectNode(AttrListNode attrList, AstTerm relation) {
    this.attrList = attrList;
    this.relation = relation;
  }

  public AttrListNode getAttrList() {
    return attrList;
  }

  public void setAttrList(AttrListNode attrList) {
    this.attrList = attrList;
  }

  public AstTerm getRelation() {
    return relation;
  }

  public void setRelation(AstTerm relation) {
    this.relation = relation;
  }

  @Override
  public String toString() {
    return "pi(" + attrList + "," + relation + ")";
  }

  @Override
  public int hashCode() {
    return (17 * attrList.hashCode()) ^ (19 * relation.hashCode());
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) return true;
    if (o instanceof ProjectNode) {
      return attrList.equals(((ProjectNode) o).attrList)
          && relation.equals(((ProjectNode) o).relation);
    }
    return false;
  }

  @Override
  public void accept(IVisitor visitor) {
    visitor.visit(this);
  }

}
