package edu.utexas.mediator.parser;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.antlr.v4.runtime.tree.AbstractParseTreeVisitor;
import org.antlr.v4.runtime.tree.TerminalNode;

import edu.utexas.mediator.ast.AndPredNode;
import edu.utexas.mediator.ast.AstNode;
import edu.utexas.mediator.ast.AstTerm;
import edu.utexas.mediator.ast.AttrDecl;
import edu.utexas.mediator.ast.AttrListNode;
import edu.utexas.mediator.ast.DeleteNode;
import edu.utexas.mediator.ast.EquiJoinNode;
import edu.utexas.mediator.ast.InPredNode;
import edu.utexas.mediator.ast.InsertNode;
import edu.utexas.mediator.ast.LopPredNode;
import edu.utexas.mediator.ast.MinusNode;
import edu.utexas.mediator.ast.NotPredNode;
import edu.utexas.mediator.ast.OrPredNode;
import edu.utexas.mediator.ast.Predicate;
import edu.utexas.mediator.ast.ProjectNode;
import edu.utexas.mediator.ast.RelDecl;
import edu.utexas.mediator.ast.RelationNode;
import edu.utexas.mediator.ast.Schema;
import edu.utexas.mediator.ast.SelectNode;
import edu.utexas.mediator.ast.TupleNode;
import edu.utexas.mediator.ast.UnionNode;
import edu.utexas.mediator.ast.UpdateNode;
import edu.utexas.mediator.parser.AntlrDslParser.AndPredContext;
import edu.utexas.mediator.parser.AntlrDslParser.AttrListContext;
import edu.utexas.mediator.parser.AntlrDslParser.DelStmtContext;
import edu.utexas.mediator.parser.AntlrDslParser.InPredContext;
import edu.utexas.mediator.parser.AntlrDslParser.InsStmtContext;
import edu.utexas.mediator.parser.AntlrDslParser.JoinExprContext;
import edu.utexas.mediator.parser.AntlrDslParser.LopPredContext;
import edu.utexas.mediator.parser.AntlrDslParser.MinusExprContext;
import edu.utexas.mediator.parser.AntlrDslParser.NotPredContext;
import edu.utexas.mediator.parser.AntlrDslParser.OrPredContext;
import edu.utexas.mediator.parser.AntlrDslParser.ProjExprContext;
import edu.utexas.mediator.parser.AntlrDslParser.SelExprContext;
import edu.utexas.mediator.parser.AntlrDslParser.StmtRootContext;
import edu.utexas.mediator.parser.AntlrDslParser.ToDelStmtContext;
import edu.utexas.mediator.parser.AntlrDslParser.ToIdContext;
import edu.utexas.mediator.parser.AntlrDslParser.ToInsStmtContext;
import edu.utexas.mediator.parser.AntlrDslParser.ToQueryExprContext;
import edu.utexas.mediator.parser.AntlrDslParser.ToUpdStmtContext;
import edu.utexas.mediator.parser.AntlrDslParser.TupleContext;
import edu.utexas.mediator.parser.AntlrDslParser.UnionExprContext;
import edu.utexas.mediator.parser.AntlrDslParser.UpdStmtContext;

public class AntlrAstGenVisitor extends AbstractParseTreeVisitor<AstNode>
    implements AntlrDslVisitor<AstNode> {

  // pass in schema to put RelDecl on RelationNode and to determine
  // primary and foreign keys for join
  private Schema schema;

  public AntlrAstGenVisitor(Schema schema) {
    this.schema = schema;
  }

  @Override
  public AstNode visitStmtRoot(StmtRootContext ctx) {
    return visit(ctx.stmt());
  }

  @Override
  public AstNode visitToQueryExpr(ToQueryExprContext ctx) {
    return visit(ctx.queryExpr());
  }

  @Override
  public AstNode visitToInsStmt(ToInsStmtContext ctx) {
    return visit(ctx.insStmt());
  }

  @Override
  public AstNode visitToDelStmt(ToDelStmtContext ctx) {
    return visit(ctx.delStmt());
  }

  @Override
  public AstNode visitToUpdStmt(ToUpdStmtContext ctx) {
    return visit(ctx.updStmt());
  }

  @Override
  public EquiJoinNode visitJoinExpr(JoinExprContext ctx) {
    AstNode lhs = visit(ctx.queryExpr(0));
    assert lhs instanceof AstTerm;
    AstNode rhs = visit(ctx.queryExpr(1));
    assert rhs instanceof AstTerm;
    EquiJoinNode join = new EquiJoinNode((AstTerm) lhs, (AstTerm) rhs);
    fillJoinAttribute(join);
    assert join.getLeftAttr() != null && join.getRightAttr() != null : join;
    return join;
  }

  @Override
  public SelectNode visitSelExpr(SelExprContext ctx) {
    AstNode pred = visit(ctx.pred());
    assert pred instanceof Predicate;
    AstNode term = visit(ctx.queryExpr());
    assert term instanceof AstTerm;
    return new SelectNode((Predicate) pred, (AstTerm) term);
  }

  @Override
  public ProjectNode visitProjExpr(ProjExprContext ctx) {
    AstNode attrList = visit(ctx.attrList());
    assert attrList instanceof AttrListNode;
    AstNode term = visit(ctx.queryExpr());
    assert term instanceof AstTerm;
    return new ProjectNode((AttrListNode) attrList, (AstTerm) term);
  }

  @Override
  public UnionNode visitUnionExpr(UnionExprContext ctx) {
    AstNode lhs = visit(ctx.queryExpr(0));
    assert lhs instanceof AstTerm;
    AstNode rhs = visit(ctx.queryExpr(1));
    assert rhs instanceof AstTerm;
    return new UnionNode((AstTerm) lhs, (AstTerm) rhs);
  }

  @Override
  public MinusNode visitMinusExpr(MinusExprContext ctx) {
    AstNode lhs = visit(ctx.queryExpr(0));
    assert lhs instanceof AstTerm;
    AstNode rhs = visit(ctx.queryExpr(1));
    assert rhs instanceof AstTerm;
    return new MinusNode((AstTerm) lhs, (AstTerm) rhs);
  }

  @Override
  public RelationNode visitToId(ToIdContext ctx) {
    return mkRelationNode(ctx.ID().getText());
  }

  @Override
  public InsertNode visitInsStmt(InsStmtContext ctx) {
    RelationNode relation = mkRelationNode(ctx.ID().getText());
    AstNode tuple = visit(ctx.tuple());
    assert tuple instanceof TupleNode;
    return new InsertNode(relation, (TupleNode) tuple);
  }

  @Override
  public DeleteNode visitDelStmt(DelStmtContext ctx) {
    RelationNode relation = mkRelationNode(ctx.ID().getText());
    AstNode pred = visit(ctx.pred());
    assert pred instanceof Predicate;
    return new DeleteNode(relation, (Predicate) pred);
  }

  @Override
  public UpdateNode visitUpdStmt(UpdStmtContext ctx) {
    RelationNode relation = mkRelationNode(ctx.ID(0).getText());
    AstNode pred = visit(ctx.pred());
    assert pred instanceof Predicate;
    String attr = ctx.ID(1).getText();
    String value = ctx.ID(2).getText();
    return new UpdateNode(relation, (Predicate) pred, attr, value);
  }

  @Override
  public AttrListNode visitAttrList(AttrListContext ctx) {
    List<String> attrList = new ArrayList<>();
    for (TerminalNode node : ctx.ID()) {
      attrList.add(node.getText());
    }
    return new AttrListNode(attrList);
  }

  @Override
  public TupleNode visitTuple(TupleContext ctx) {
    List<String> values = new ArrayList<>();
    for (TerminalNode node : ctx.ID()) {
      values.add(node.getText());
    }
    return new TupleNode(values);
  }

  @Override
  public NotPredNode visitNotPred(NotPredContext ctx) {
    AstNode pred = visit(ctx.pred());
    assert pred instanceof Predicate;
    return new NotPredNode((Predicate) pred);
  }

  @Override
  public AndPredNode visitAndPred(AndPredContext ctx) {
    AstNode lhs = visit(ctx.pred(0));
    assert lhs instanceof Predicate;
    AstNode rhs = visit(ctx.pred(1));
    assert rhs instanceof Predicate;
    return new AndPredNode((Predicate) lhs, (Predicate) rhs);
  }

  @Override
  public OrPredNode visitOrPred(OrPredContext ctx) {
    AstNode lhs = visit(ctx.pred(0));
    assert lhs instanceof Predicate;
    AstNode rhs = visit(ctx.pred(1));
    assert rhs instanceof Predicate;
    return new OrPredNode((Predicate) lhs, (Predicate) rhs);
  }

  @Override
  public InPredNode visitInPred(InPredContext ctx) {
    AstNode term = visit(ctx.queryExpr());
    assert term instanceof AstTerm;
    return new InPredNode(ctx.ID().getText(), (AstTerm) term);
  }

  @Override
  public LopPredNode visitLopPred(LopPredContext ctx) {
    return new LopPredNode(ctx.LOP().getText(), ctx.ID(0).getText(), ctx.ID(1).getText());
  }

  private RelationNode mkRelationNode(String name) {
    RelDecl rel = schema.getRelDeclByName(name);
    return new RelationNode(name, rel);
  }

  private void fillJoinAttribute(EquiJoinNode join) {
    Set<RelDecl> lhsRels = new HashSet<>();
    findAllRelationsInTerm(join.getLhs()).forEach((rel) -> lhsRels.add(rel.getRelDecl()));
    Set<RelDecl> rhsRels = new HashSet<>();
    findAllRelationsInTerm(join.getRhs()).forEach((rel) -> rhsRels.add(rel.getRelDecl()));

    for (RelDecl rel : lhsRels) {
      for (AttrDecl attr : rel.getAttrDecls()) {
        if (!attr.isForeignKey()) continue;
        AttrDecl refAttr = attr.getReferenceAttrDecl();
        if (rhsRels.contains(refAttr.getRelDecl())) {
          join.setLeftAttr(attr.getName());
          join.setRightAttr(refAttr.getName());
          return;
        }
      }
    }
    for (RelDecl rel : rhsRels) {
      for (AttrDecl attr : rel.getAttrDecls()) {
        if (!attr.isForeignKey()) continue;
        AttrDecl refAttr = attr.getReferenceAttrDecl();
        if (lhsRels.contains(refAttr.getRelDecl())) {
          join.setLeftAttr(refAttr.getName());
          join.setRightAttr(attr.getName());
          return;
        }
      }
    }
  }

  private Set<RelationNode> findAllRelationsInTerm(AstTerm term) {
    // TODO may not be completely correct if projection changes the available
    // attributes for a term in general
    Set<RelationNode> ret = new HashSet<>();
    if (term instanceof RelationNode) {
      ret.add((RelationNode) term);
    } else if (term instanceof ProjectNode) {
      ret.addAll(findAllRelationsInTerm(((ProjectNode) term).getRelation()));
    } else if (term instanceof SelectNode) {
      ret.addAll(findAllRelationsInTerm(((SelectNode) term).getRelation()));
    } else if (term instanceof EquiJoinNode) {
      ret.addAll(findAllRelationsInTerm(((EquiJoinNode) term).getLhs()));
      ret.addAll(findAllRelationsInTerm(((EquiJoinNode) term).getRhs()));
    } else if (term instanceof UnionNode) {
      ret.addAll(findAllRelationsInTerm(((UnionNode) term).getLhs()));
      ret.addAll(findAllRelationsInTerm(((UnionNode) term).getRhs()));
    } else if (term instanceof MinusNode) {
      ret.addAll(findAllRelationsInTerm(((MinusNode) term).getLhs()));
      ret.addAll(findAllRelationsInTerm(((MinusNode) term).getRhs()));
    } else {
      throw new RuntimeException("Unknown subtype of AstTerm");
    }
    return ret;
  }

}
