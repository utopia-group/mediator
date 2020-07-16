package edu.utexas.mediator.ast;

import java.util.List;

public class Transaction implements IVisitable {

  private Signature signature;
  private List<AstNode> statements;

  public Transaction(Signature signature, List<AstNode> statements) {
    this.signature = signature;
    this.statements = statements;
  }

  public Signature getSignature() {
    return signature;
  }

  public void setSignature(Signature signature) {
    this.signature = signature;
  }

  public List<AstNode> getStatements() {
    return statements;
  }

  public void setStatements(List<AstNode> statements) {
    this.statements = statements;
  }

  @Override
  public String toString() {
    return "\n" + signature + "\n" + statements;
  }

  @Override
  public int hashCode() {
    return signature.hashCode() + statements.hashCode();
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) return true;
    if (o instanceof Transaction) {
      return signature.equals(((Transaction) o).signature)
          && statements.equals(((Transaction) o).statements);
    }
    return false;
  }

  @Override
  public void accept(IVisitor visitor) {
    visitor.visit(this);
  }

}
