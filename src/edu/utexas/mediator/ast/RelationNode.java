package edu.utexas.mediator.ast;

public class RelationNode extends AstTerm {

  private String name;
  private RelDecl relDecl;

  public RelationNode(String name, RelDecl relDecl) {
    this.name = name;
    this.relDecl = relDecl;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public RelDecl getRelDecl() {
    return relDecl;
  }

  public void setRelDecl(RelDecl relDecl) {
    this.relDecl = relDecl;
  }

  @Override
  public String toString() {
    return name;
  }

  @Override
  public int hashCode() {
    return name.hashCode();
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) return true;
    if (o instanceof RelationNode) return name.equals(((RelationNode) o).name);
    return false;
  }

  @Override
  public void accept(IVisitor visitor) {
    visitor.visit(this);
  }

}
