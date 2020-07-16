package edu.utexas.mediator.core;

import java.util.HashSet;
import java.util.Set;

import edu.utexas.mediator.ast.AndPredNode;
import edu.utexas.mediator.ast.AttrDecl;
import edu.utexas.mediator.ast.AttrListNode;
import edu.utexas.mediator.ast.DeleteNode;
import edu.utexas.mediator.ast.EquiJoinNode;
import edu.utexas.mediator.ast.IVisitor;
import edu.utexas.mediator.ast.InPredNode;
import edu.utexas.mediator.ast.InsertNode;
import edu.utexas.mediator.ast.LopPredNode;
import edu.utexas.mediator.ast.MinusNode;
import edu.utexas.mediator.ast.NotPredNode;
import edu.utexas.mediator.ast.OrPredNode;
import edu.utexas.mediator.ast.Program;
import edu.utexas.mediator.ast.ProjectNode;
import edu.utexas.mediator.ast.RelDecl;
import edu.utexas.mediator.ast.RelationNode;
import edu.utexas.mediator.ast.Schema;
import edu.utexas.mediator.ast.SelectNode;
import edu.utexas.mediator.ast.Signature;
import edu.utexas.mediator.ast.Transaction;
import edu.utexas.mediator.ast.TupleNode;
import edu.utexas.mediator.ast.UnionNode;
import edu.utexas.mediator.ast.UpdateNode;

public class IdentifierChecker implements IVisitor {

  private Schema schema;
  private Set<String> attrs = new HashSet<>();
  private Set<String> curVars;

  @Override
  public void visit(Program program) {
    program.getSchema().accept(this);
    program.getTransactions().forEach((tran) -> tran.accept(this));
  }

  @Override
  public void visit(Schema schema) {
    this.schema = schema;
    schema.getRelDecls().forEach((relDecl) -> relDecl.accept(this));
  }

  @Override
  public void visit(RelDecl relDecl) {
    relDecl.getAttrDecls().forEach((attrDecl) -> attrDecl.accept(this));
  }

  @Override
  public void visit(AttrDecl attrDecl) {
    // TODO attribute names could be duplicated - should use qualified name
    attrs.add(attrDecl.getName());
  }

  @Override
  public void visit(Transaction tran) {
    tran.getSignature().accept(this);
    tran.getStatements().forEach((stmt) -> stmt.accept(this));
  }

  @Override
  public void visit(Signature sig) {
    curVars = new HashSet<>();
    sig.getArguments().forEach((arg) -> curVars.add(arg));
  }

  @Override
  public void visit(InsertNode ins) {
    ins.getRelation().accept(this);
    ins.getTuple().accept(this);
  }

  @Override
  public void visit(DeleteNode del) {
    del.getRelation().accept(this);
    del.getPred().accept(this);
  }

  @Override
  public void visit(UpdateNode upd) {
    upd.getRelation().accept(this);
    upd.getPred().accept(this);
    checkAttribute(upd.getAttr());
    checkVariable(upd.getValue());
  }

  @Override
  public void visit(TupleNode tuple) {
    tuple.getValues().forEach((value) -> checkVariable(value));
  }

  @Override
  public void visit(AttrListNode attrList) {
    attrList.getAttributes().forEach((attr) -> checkAttribute(attr));
  }

  @Override
  public void visit(RelationNode rel) {
    checkRelation(rel.getName());
  }

  @Override
  public void visit(ProjectNode proj) {
    proj.getRelation().accept(this);
    proj.getAttrList().accept(this);
  }

  @Override
  public void visit(SelectNode sel) {
    sel.getRelation().accept(this);
    sel.getPred().accept(this);
  }

  @Override
  public void visit(EquiJoinNode join) {
    join.getLhs().accept(this);
    join.getRhs().accept(this);
    checkAttribute(join.getLeftAttr());
    checkAttribute(join.getRightAttr());
  }

  @Override
  public void visit(UnionNode union) {
    union.getLhs().accept(this);
    union.getRhs().accept(this);
  }

  @Override
  public void visit(MinusNode minus) {
    minus.getLhs().accept(this);
    minus.getRhs().accept(this);
  }

  @Override
  public void visit(LopPredNode lop) {
    checkAttribute(lop.getLhs());
    // TODO - check rhs if it is also an attribute
    checkVariable(lop.getRhs());
  }

  @Override
  public void visit(InPredNode in) {
    checkAttribute(in.getLhs());
    in.getRhsExpr().accept(this);
  }

  @Override
  public void visit(AndPredNode and) {
    and.getLhs().accept(this);
    and.getRhs().accept(this);
  }

  @Override
  public void visit(OrPredNode or) {
    or.getLhs().accept(this);
    or.getRhs().accept(this);
  }

  @Override
  public void visit(NotPredNode not) {
    not.getPred().accept(this);
  }

  private void checkRelation(String rel) {
    if (!schema.containsRelName(rel)) {
      throw new RuntimeException("relation '" + rel + "' is undefined");
    }
  }

  private void checkAttribute(String attr) {
    if (!attrs.contains(attr)) {
      throw new RuntimeException("attribute '" + attr + "' is undefined");
    }
  }

  private void checkVariable(String var) {
    if (!curVars.contains(var) && !var.startsWith("UUID_")) {
      throw new RuntimeException("identifier '" + var + "' is undefined");
    }
  }

}
