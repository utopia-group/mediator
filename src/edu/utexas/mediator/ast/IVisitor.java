package edu.utexas.mediator.ast;

public interface IVisitor {

  public void visit(Program program);

  public void visit(Schema schema);

  public void visit(RelDecl relDecl);

  public void visit(AttrDecl attrDecl);

  public void visit(Transaction tran);

  public void visit(Signature sig);

  public void visit(InsertNode ins);

  public void visit(DeleteNode del);

  public void visit(UpdateNode upd);

  public void visit(TupleNode tuple);

  public void visit(AttrListNode attrList);

  public void visit(RelationNode rel);

  public void visit(ProjectNode proj);

  public void visit(SelectNode sel);

  public void visit(EquiJoinNode join);

  public void visit(UnionNode union);

  public void visit(MinusNode minus);

  public void visit(LopPredNode lop);

  public void visit(InPredNode in);

  public void visit(AndPredNode and);

  public void visit(OrPredNode or);

  public void visit(NotPredNode not);

}
