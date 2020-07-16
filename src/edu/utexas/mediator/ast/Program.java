package edu.utexas.mediator.ast;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Program implements IVisitable {

  private Schema schema;
  private List<Transaction> transactions;
  private Map<String, Transaction> lookup;

  public Program(Schema schema, List<Transaction> trans) {
    this.schema = schema;
    setTransactions(trans);
  }

  public Schema getSchema() {
    return schema;
  }

  public void setSchema(Schema schema) {
    this.schema = schema;
  }

  public List<Transaction> getTransactions() {
    return transactions;
  }

  public boolean containsTransaction(String name) {
    return lookup.containsKey(name);
  }

  public Transaction getTransactionByName(String name) {
    assert lookup.containsKey(name);
    return lookup.get(name);
  }

  public void setTransactions(List<Transaction> transactions) {
    this.transactions = transactions;
    lookup = buildLookupTable(transactions);
  }

  @Override
  public String toString() {
    return schema + "\n" + transactions;
  }

  @Override
  public int hashCode() {
    return schema.hashCode() + transactions.hashCode();
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) return true;
    if (o instanceof Program) {
      return schema.equals(((Program) o).schema) && transactions.equals(((Program) o).transactions);
    }
    return false;
  }

  @Override
  public void accept(IVisitor visitor) {
    visitor.visit(this);
  }

  private Map<String, Transaction> buildLookupTable(List<Transaction> trans) {
    Map<String, Transaction> map = new HashMap<>();
    for (Transaction tran : trans) {
      map.put(tran.getSignature().getName(), tran);
    }
    return map;
  }

}
