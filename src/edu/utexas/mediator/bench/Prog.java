package edu.utexas.mediator.bench;

import java.util.List;

public class Prog {

  private List<String> relations;
  private List<String> primarykeys;
  private List<String> foreignkeys;
  private List<Tran> transactions;

  public List<String> getRelations() {
    return relations;
  }

  public void setRelations(List<String> relations) {
    this.relations = relations;
  }

  public List<String> getPrimarykeys() {
    return primarykeys;
  }

  public void setPrimarykeys(List<String> primarykeys) {
    this.primarykeys = primarykeys;
  }

  public List<String> getForeignkeys() {
    return foreignkeys;
  }

  public void setForeignkeys(List<String> foreignkeys) {
    this.foreignkeys = foreignkeys;
  }

  public List<Tran> getTransactions() {
    return transactions;
  }

  public void setTransactions(List<Tran> transactions) {
    this.transactions = transactions;
  }

}
