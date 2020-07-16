package edu.utexas.mediator.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

public class ProgramRenamer implements IVisitor {

  private final String suffix;

  public ProgramRenamer(String suffix) {
    this.suffix = suffix;
  }

  @Override
  public void visit(Program program) {
    program.getSchema().accept(this);
    program.getTransactions().forEach((tran) -> tran.accept(this));
  }

  @Override
  public void visit(Schema schema) {
    schema.getRelDecls().forEach((relDecl) -> relDecl.accept(this));
    Map<String, RelDecl> lookup = new HashMap<>();
    schema.getLookup().entrySet()
        .forEach(entry -> lookup.put(entry.getKey() + suffix, entry.getValue()));
    schema.setLookup(lookup);
  }

  @Override
  public void visit(RelDecl relDecl) {
    relDecl.setName(relDecl.getName() + suffix);
    relDecl.getAttrDecls().forEach((attrDecl) -> attrDecl.accept(this));
    Map<String, AttrDecl> lookup = new HashMap<>();
    relDecl.getLookup().entrySet()
        .forEach(entry -> lookup.put(entry.getKey() + suffix, entry.getValue()));
    relDecl.setLookup(lookup);
  }

  @Override
  public void visit(AttrDecl attrDecl) {
    attrDecl.setName(attrDecl.getName() + suffix);
  }

  @Override
  public void visit(Transaction tran) {
    tran.getSignature().accept(this);
    tran.getStatements().forEach((stmt) -> stmt.accept(this));
  }

  @Override
  public void visit(Signature sig) {
    ; // empty
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
    upd.setAttr(upd.getAttr() + suffix);
  }

  @Override
  public void visit(TupleNode tuple) {
    ; // empty
  }

  @Override
  public void visit(AttrListNode attrList) {
    List<String> attributes = new ArrayList<>();
    attrList.getAttributes().forEach((attr) -> attributes.add(attr + suffix));
    attrList.setAttrList(attributes);
  }

  @Override
  public void visit(RelationNode rel) {
    rel.setName(rel.getName() + suffix);
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
    join.setLeftAttr(join.getLeftAttr() + suffix);
    join.setRightAttr(join.getRightAttr() + suffix);
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
    // TODO - rename rhs if it is also an attribute
    lop.setLhs(lop.getLhs() + suffix);
  }

  @Override
  public void visit(InPredNode in) {
    in.setLhs(in.getLhs() + suffix);
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

}
