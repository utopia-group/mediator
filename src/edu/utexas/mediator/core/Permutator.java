package edu.utexas.mediator.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.utexas.mediator.constraint.Constraint;
import edu.utexas.mediator.constraint.Context;
import edu.utexas.mediator.constraint.LEqTerm;
import edu.utexas.mediator.constraint.LImply;
import edu.utexas.mediator.constraint.LProject;

public class Permutator {

  private Context ctx;

  public Permutator() {
    ctx = new Context();
  }

  public List<Constraint> veriCondPerm(Constraint cstr) {
    List<Constraint> res = new ArrayList<>();
    assert cstr instanceof LImply;
    LImply imply = (LImply) cstr;
    assert imply.getRhs() instanceof LEqTerm;
    Constraint lhs = imply.getLhs();
    LEqTerm rhs = (LEqTerm) imply.getRhs();
    assert rhs.getLhs() instanceof LProject;
    assert rhs.getRhs() instanceof LProject;
    LProject srcProj = (LProject) rhs.getLhs();
    LProject tgtProj = (LProject) rhs.getRhs();
    List<String> attrList = tgtProj.getAttrList().getAttributes();
    List<List<String>> attrListPerms = permute(attrList, srcProj.getAttrList().getAttrNum());
    for (List<String> attrs : attrListPerms) {
      LProject newTgtProj = ctx.mkProject(ctx.mkAttrList(attrs), tgtProj.getRelation());
      res.add(ctx.mkImply(lhs, ctx.mkEqTerm(srcProj, newTgtProj)));
    }
    return res;
  }

  public List<List<String>> permute(List<String> list, int len) {
    String[] strs = new String[list.size()];
    strs = list.toArray(strs);
    Set<List<String>> set = new HashSet<>();
    permute(strs, len, 0, set);
    List<List<String>> res = new ArrayList<>();
    List<String> orig = Arrays.asList(Arrays.copyOfRange(strs, 0, len));
    res.add(orig);
    set.remove(orig);
    res.addAll(set);
    return res;
  }

  private void permute(String[] strs, int len, int start, Set<List<String>> res) {
    if (start >= len) {
      res.add(new ArrayList<>(Arrays.asList(Arrays.copyOfRange(strs, 0, len))));
    } else {
      for (int i = start; i < strs.length; ++i) {
        swap(strs, start, i);
        permute(strs, len, start + 1, res);
        swap(strs, start, i);
      }
    }
  }

  private void swap(String[] strs, int i, int j) {
    String temp = strs[i];
    strs[i] = strs[j];
    strs[j] = temp;
  }

}
