package edu.utexas.mediator.solver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import edu.utexas.mediator.LoggerWrapper;
import edu.utexas.mediator.Options;
import edu.utexas.mediator.ast.AttrDecl;
import edu.utexas.mediator.ast.RelDecl;
import edu.utexas.mediator.constraint.Constraint;
import edu.utexas.mediator.constraint.Invariant;
import edu.utexas.mediator.constraint.LEqTerm;
import edu.utexas.mediator.constraint.LEquiJoin;
import edu.utexas.mediator.constraint.LImply;
import edu.utexas.mediator.constraint.LPred;
import edu.utexas.mediator.constraint.LPredAttrVal;
import edu.utexas.mediator.constraint.LProject;
import edu.utexas.mediator.constraint.LRelConst;
import edu.utexas.mediator.constraint.LSelect;
import edu.utexas.mediator.constraint.LTerm;
import edu.utexas.mediator.core.ISolver;

/**
 * A solver for checking constraints of the form:
 * conjunctive projection equality premises imply equality of conjunctive queries.
 * <p>
 * The high level idea is to convert conjunctive queries into the format of
 * labeled tuples (like datalog). Then exhaustively apply the premises to rewrite
 * the LHS representation and check if it is isomorphic to the RHS representation.
 */
public class UcqSolver implements ISolver {

  private static final Logger LOGGER = LoggerWrapper.getInstance();

  private static enum Status {
    VALID, UNKNOWN, INAPPLICABLE
  }

  // suffix of source attributes
  private static final String S_SUF = "_s";
  // queried values start with "x_"
  private static final String X = "x_";
  // other values start with "y_"
  private static final String Y = "y_";
  private int varCount = 0;
  private List<Rule> premises;

  public UcqSolver() {
    premises = new ArrayList<>();
  }

  @Override
  public boolean isValid(Constraint formula) {
    Status status = check(formula);
    if (Options.UCQ_SOLVER_VERBOSE) {
      LOGGER.fine(String.format("UCQ Solver Status: %s", status));
    }
    return status == Status.VALID;
  }

  public Status check(Constraint cstr) {
    if (!(cstr instanceof LImply)) return Status.INAPPLICABLE;
    Constraint lhs = ((LImply) cstr).getLhs();
    Constraint rhs = ((LImply) cstr).getRhs();
    if (!(lhs instanceof Invariant && rhs instanceof LEqTerm)) return Status.INAPPLICABLE;
    return check((Invariant) lhs, (LEqTerm) rhs);
  }

  /**
   * Check a particular form of implication. Assume all relations have unique names.
   * Assume LHS is from source program and RHS is from target program.
   *
   * @param inv
   *          the invariant, which is a list of conjuncts
   * @param concl
   *          the equality to check
   * @return status
   */
  public Status check(Invariant inv, LEqTerm concl) {
    for (Constraint cstr : inv.getConjuncts()) {
      assert cstr instanceof LEqTerm;
      parseEqTerm((LEqTerm) cstr);
    }
    assert concl.getLhs() instanceof LProject;
    assert concl.getRhs() instanceof LProject;
    Graph src = parseQuery((LProject) concl.getLhs());
    Graph tgt = parseQuery((LProject) concl.getRhs());
    if (Options.UCQ_SOLVER_VERBOSE) {
      LOGGER.fine(String.format("Premises: \n%s", premises.stream()
          .map(Rule::toString)
          .collect(Collectors.joining(System.lineSeparator()))));
      LOGGER.fine(String.format("Source: %s", src));
      LOGGER.fine(String.format("Target: %s", tgt));
    }
    // TODO: should we checkIsomorphism(src, tgt) before check(src, tgt)?
    // maybe not, because we are trying to rewrite src to tgt
    return check(src, tgt);
  }

  private Status check(Graph src, Graph tgt) {
    Set<RelNode> nodes = findAllSourceNodes(src);
    List<Rule> rules = findAllRules(nodes);
    for (Rule rule : rules) {
      Graph graph = applyRule(src, rule);
      if (checkIsomorphism(graph, tgt)) return Status.VALID;
      if (check(graph, tgt) == Status.VALID) return Status.VALID;
    }
    return Status.UNKNOWN;
  }

  /**
   * Check if two graphs are isomorphic. Isomorphism:
   * (1) source non-Y labels must map to the same target labels
   * (2) one source Y label must always map to identical target labels
   *
   * @param src
   *          the source graph
   * @param tgt
   *          the target graph
   * @return {@code true} if two graphs are isomorphic
   */
  private boolean checkIsomorphism(Graph src, Graph tgt) {
    // optimization - src can NOT contain any original source node
    // because we are trying to rewrite it to tgt
    if (!findAllSourceNodes(src).isEmpty()) return false;
    if (src.getRels().size() != tgt.getRels().size()) return false;
    Map<String, String> labelMap = new HashMap<>();

    for (RelNode srcRel : src.getRels()) {
      if (!tgt.containsRel(srcRel.getName())) return false;
      RelNode tgtRel = tgt.findRelByName(srcRel.getName());
      if (srcRel.getAttrs().size() != tgtRel.getAttrs().size()) return false;
      for (AttrNode srcAttr : srcRel.getAttrs()) {
        if (!tgtRel.containsAttr(srcAttr.getName())) return false;
        AttrNode tgtAttr = tgtRel.findAttrByName(srcAttr.getName());
        String srcLabel = srcAttr.getLabel();
        String tgtLabel = tgtAttr.getLabel();
        if (srcLabel.startsWith(Y)) {
          if (labelMap.containsKey(srcLabel)) {
            if (!tgtLabel.equals(labelMap.get(srcLabel))) return false;
          } else {
            if (labelMap.containsKey(tgtLabel)) return false;
            // TODO: confirm removing the following line is correct
            // if (!tgtLabel.startsWith(Y)) return false;
            labelMap.put(srcLabel, tgtLabel);
          }
        } else {
          if (!srcLabel.equals(tgtLabel)) return false;
        }
      }
    }
    return true;
  }

  private Set<RelNode> findAllSourceNodes(Graph graph) {
    Set<RelNode> res = new HashSet<>();
    for (RelNode rel : graph.getRels()) {
      if (rel.getName().endsWith(S_SUF)) {
        res.add(rel);
      }
    }
    return res;
  }

  private List<Rule> findAllRules(Set<RelNode> nodes) {
    // find all rules that are applicable for the current graph
    List<Rule> res = new ArrayList<>();
    for (Rule rule : premises) {
      boolean contains = true;
      for (RelNode rel : rule.getSrcGraph().getRels()) {
        if (!nodes.contains(rel)) contains = false;
      }
      if (contains) res.add(rule);
    }
    return res;
  }

  private Graph applyRule(Graph graph, Rule rule) {
    // assume: rule is applicable on graph
    Unifier unifier = unify(rule.getSrcGraph(), graph);
    Graph res = deepClone(graph);
    // generate new target graph
    Graph tgtGraph = deepClone(rule.getTgtGraph());
    for (RelNode rel : tgtGraph.getRels()) {
      for (AttrNode attr : rel.getAttrs()) {
        if (unifier.containsKey(attr.getLabel())) {
          attr.setLabel(unifier.mapsTo(attr.getLabel()));
        }
      }
    }
    // replace the nodes
    rule.getSrcGraph().getRels().forEach((rel) -> res.removeRel(rel.getName()));
    // FIXME - prove it is correct, if two different graphs in source
    // map to the same graph in target, the former one will be replaced
    // but since the searching procedure is back-tracking (?), it is still correct
    tgtGraph.getRels().forEach((rel) -> res.addRelOrReplace(rel));
    return res;
  }

  private Graph deepClone(Graph graph) {
    Graph res = new Graph();
    for (RelNode rel : graph.getRels()) {
      RelNode resRel = new RelNode(rel.getName(), rel.getRelDecl());
      for (AttrNode attr : rel.getAttrs()) {
        AttrNode resAttr = new AttrNode(attr.getIndex(), attr.getName(), attr.getLabel());
        resRel.addAttr(resAttr);
      }
      res.addRel(resRel);
    }
    return res;
  }

  private Unifier unify(Graph src, Graph tgt) {
    Unifier unifier = new Unifier();
    for (RelNode srcRel : src.getRels()) {
      assert tgt.containsRel(srcRel.getName()) : srcRel.getName() + " not found";
      RelNode tgtRel = tgt.findRelByName(srcRel.getName());
      for (AttrNode srcAttr : srcRel.getAttrs()) {
        assert tgtRel.containsAttr(srcAttr.getName()) : srcAttr.getName() + " not found";
        AttrNode tgtAttr = tgtRel.findAttrByName(srcAttr.getName());
        unifier.addMapping(srcAttr.getLabel(), tgtAttr.getLabel());
      }
    }
    return unifier;
  }

  private void parseEqTerm(LEqTerm eq) {
    assert eq.getLhs() instanceof LProject;
    assert eq.getRhs() instanceof LProject;
    Graph src = parseQuery((LProject) eq.getLhs());
    Graph tgt = parseQuery((LProject) eq.getRhs());
    premises.add(new Rule(src, tgt));
  }

  private Graph parseQuery(LProject proj) {
    LTerm term = proj.getRelation();
    List<RelNode> rels;
    if (term instanceof LRelConst) {
      rels = parseQuery((LRelConst) term);
    } else if (term instanceof LEquiJoin) {
      rels = parseQuery((LEquiJoin) term);
    } else if (term instanceof LSelect) {
      rels = parseQuery((LSelect) term);
    } else {
      throw new RuntimeException("Unsupported query");
    }
    // assign X's
    int index = 0;
    for (String attrName : proj.getAttrList().getAttributes()) {
      AttrNode attr = findAttrByName(rels, attrName);
      // TODO priority of different types of labels
      String label = attr.getLabel();
      if (label == null) {
        attr.setLabel(X + String.valueOf(index));
      } else if (label.startsWith(Y)) {
        // replace all y's to x_index
        replaceAllLabels(rels, X + String.valueOf(index), label);
      } else {
        if (Options.UCQ_SOLVER_VERBOSE) {
          LOGGER.fine(String.format("Label conflication: %s -> %s in %s", attrName, attr.getLabel(), proj));
        }
      }
      ++index;
    }
    // assign Y's
    for (RelNode rel : rels) {
      for (AttrNode attr : rel.getAttrs()) {
        if (attr.getLabel() == null) {
          attr.setLabel(getFreshVar());
        }
      }
    }
    Graph graph = new Graph();
    rels.forEach((rel) -> graph.addRel(rel));
    return graph;
  }

  private List<RelNode> parseQuery(LSelect sel) {
    LTerm term = sel.getRelation();
    LPred pred = sel.getPred();
    List<RelNode> rels;
    if (term instanceof LRelConst) {
      rels = parseQuery((LRelConst) term);
    } else if (term instanceof LEquiJoin) {
      rels = parseQuery((LEquiJoin) term);
    } else if (term instanceof LSelect) {
      rels = parseQuery((LSelect) term);
    } else {
      throw new RuntimeException("Unsupported query");
    }
    if (pred instanceof LPredAttrVal) {
      String attrName = ((LPredAttrVal) pred).getAttribute().getName();
      String val = ((LPredAttrVal) pred).getValue().getName();
      AttrNode attr = findAttrByName(rels, attrName);
      String prevLabel = attr.getLabel();
      attr.setLabel(val);
      // assign associated foreign keys to the same label
      replaceAllLabels(rels, val, prevLabel);
    } else {
      throw new RuntimeException("Unsupported predicate");
    }
    return rels;
  }

  /**
   * Set all nodes with a given old label to a new label
   *
   * @param rels
   *          a list of relation nodes
   * @param newLabel
   *          the new label
   * @param oldLabel
   *          the old label
   */
  private void replaceAllLabels(List<RelNode> rels, String newLabel, String oldLabel) {
    for (RelNode rel : rels) {
      for (AttrNode attrNode : rel.getAttrs()) {
        if (attrNode.getLabel() != null && attrNode.getLabel().equals(oldLabel)) {
          attrNode.setLabel(newLabel);
        }
      }
    }
  }

  private List<RelNode> parseQuery(LEquiJoin join) {
    List<RelNode> res = new ArrayList<>();
    LTerm lhs = join.getLhs();
    LTerm rhs = join.getRhs();
    List<RelNode> lhsRes, rhsRes;
    if (lhs instanceof LEquiJoin && rhs instanceof LEquiJoin) {
      lhsRes = parseQuery((LEquiJoin) lhs);
      rhsRes = parseQuery((LEquiJoin) rhs);
    } else if (lhs instanceof LEquiJoin && rhs instanceof LRelConst) {
      lhsRes = parseQuery((LEquiJoin) lhs);
      rhsRes = parseQuery((LRelConst) rhs);
    } else if (lhs instanceof LRelConst && rhs instanceof LEquiJoin) {
      lhsRes = parseQuery((LRelConst) lhs);
      rhsRes = parseQuery((LEquiJoin) rhs);
    } else if (lhs instanceof LRelConst && rhs instanceof LRelConst) {
      lhsRes = parseQuery((LRelConst) lhs);
      rhsRes = parseQuery((LRelConst) rhs);
    } else {
      throw new RuntimeException("Unsupported query");
    }

    AttrNode lhsAttr = findAttrByName(lhsRes, join.getLeftAttr().getName());
    AttrNode rhsAttr = findAttrByName(rhsRes, join.getRightAttr().getName());
    if (lhsAttr.getLabel() != null) {
      assert rhsAttr.getLabel() == null || rhsAttr.getLabel() == lhsAttr.getLabel();
      rhsAttr.setLabel(lhsAttr.getLabel());
    } else if (rhsAttr.getLabel() != null) {
      lhsAttr.setLabel(rhsAttr.getLabel());
    } else {
      String label = getFreshVar();
      lhsAttr.setLabel(label);
      rhsAttr.setLabel(label);
    }
    res.addAll(lhsRes);
    res.addAll(rhsRes);
    enforceSameLabelForAllFKRKs(res);
    return res;
  }

  /**
   * Enforce all (foreign key, reference key) pairs between LHS and RHS of the equi-join
   * to be assigned by the same label
   *
   * @param rels
   *          a list of relation nodes
   */
  private void enforceSameLabelForAllFKRKs(List<RelNode> rels) {
    Set<String> relNames = rels.stream()
        .map(RelNode::getName)
        .collect(Collectors.toSet());
    for (int i = 0; i < rels.size(); ++i) {
      RelNode rel = rels.get(i);
      RelDecl relDecl = rel.getRelDecl();
      for (AttrDecl attrDecl : relDecl.getAttrDecls()) {
        if (attrDecl.isForeignKey()) {
          AttrDecl refAttrDecl = attrDecl.getReferenceAttrDecl();
          if (relNames.contains(refAttrDecl.getRelDecl().getName())) {
            AttrNode attrNode = findAttrByName(rels, attrDecl.getName());
            AttrNode refAttrNode = findAttrByName(rels, refAttrDecl.getName());
            enforceSameLabel(attrNode, refAttrNode);
          }
        }
      }
    }
  }

  private void enforceSameLabel(AttrNode attr1, AttrNode attr2) {
    String label1 = attr1.getLabel();
    String label2 = attr2.getLabel();
    if (label1 == null && label2 == null) {
      String label = getFreshVar();
      attr1.setLabel(label);
      attr2.setLabel(label);
      return;
    } else if (label1 == null) {
      attr1.setLabel(label2);
      return;
    } else if (label2 == null) {
      attr2.setLabel(label1);
      return;
    }
    boolean isParam1 = isParameterLabel(label1);
    boolean isParam2 = isParameterLabel(label2);
    if (isParam1 && isParam2) {
      if (!label1.equals(label2)) throw new IllegalStateException();
    } else if (isParam1 && !isParam2) {
      attr2.setLabel(label1);
    } else if (!isParam1 && isParam2) {
      attr1.setLabel(label2);
    } else {
      int comp = label1.compareTo(label2);
      if (comp < 0) {
        attr2.setLabel(label1);
      } else if (comp > 0) {
        attr1.setLabel(label2);
      } else {
        ; // do nothing
      }
    }
  }

  private boolean isParameterLabel(String label) {
    return !label.startsWith(X) && !label.startsWith(Y);
  }

  private List<RelNode> parseQuery(LRelConst rel) {
    List<RelNode> res = new ArrayList<>();
    RelNode node = new RelNode(rel.getName(), rel.getRelDecl());
    int index = 0;
    for (AttrDecl attr : rel.getRelDecl().getAttrDecls()) {
      node.addAttr(new AttrNode(index, attr.getName()));
      ++index;
    }
    res.add(node);
    return res;
  }

  private AttrNode findAttrByName(List<RelNode> rels, String name) {
    for (RelNode rel : rels) {
      if (rel.containsAttr(name)) {
        return rel.findAttrByName(name);
      }
    }
    throw new IllegalArgumentException(String.format("Cannot find attribute (%s) in %s", name, rels));
  }

  private String getFreshVar() {
    return Y + String.valueOf(varCount++);
  }

}
