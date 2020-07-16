package edu.utexas.mediator.ast;

public class AttrDecl implements IVisitable {

  private String type;
  private String name;
  private RelDecl relDecl;
  private boolean primaryKey;
  private boolean foreignKey;
  private AttrDecl referenceAttrDecl;

  public AttrDecl(String type, String name) {
    this.type = type;
    this.name = name;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
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

  public boolean isPrimaryKey() {
    return primaryKey;
  }

  public void setPrimaryKey(boolean primaryKey) {
    this.primaryKey = primaryKey;
  }

  public boolean isForeignKey() {
    return foreignKey;
  }

  public void setForeignKey(boolean foreignKey) {
    this.foreignKey = foreignKey;
  }

  public AttrDecl getReferenceAttrDecl() {
    return referenceAttrDecl;
  }

  public void setReferenceAttrDecl(AttrDecl referenceAttrDecl) {
    this.referenceAttrDecl = referenceAttrDecl;
  }

  @Override
  public String toString() {
    return type + " " + name;
  }

  @Override
  public int hashCode() {
    return type.hashCode() + name.hashCode();
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) return true;
    if (o instanceof AttrDecl) {
      return type.equals(((AttrDecl) o).type) && name.equals(((AttrDecl) o).name)
          && relDecl.equals(((AttrDecl) o).relDecl);
    }
    return false;
  }

  @Override
  public void accept(IVisitor visitor) {
    visitor.visit(this);
  }

}
