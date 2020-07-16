package edu.utexas.mediator;

public class Enumerations {

  public enum Operator {
    EQ, NE, LT, LE, GT, GE
  };

  public enum TranType {
    INSERTION, DELETION, UPDATE, QUERY, UNKNOWN
  }

  public static Operator stringToOperator(String op) {
    switch (op) {
    case "=":
      return Operator.EQ;
    case "!=":
      return Operator.NE;
    case "<":
      return Operator.LT;
    case "<=":
      return Operator.LE;
    case ">":
      return Operator.GT;
    case ">=":
      return Operator.GE;
    default:
      throw new RuntimeException("Unknown operator string");
    }
  }

  public static String operatorToString(Operator op) {
    switch (op) {
    case EQ:
      return "=";
    case NE:
      return "!=";
    case LT:
      return "<";
    case LE:
      return "<=";
    case GT:
      return ">";
    case GE:
      return ">=";
    default:
      throw new RuntimeException("Unknown operator");
    }
  }

}
