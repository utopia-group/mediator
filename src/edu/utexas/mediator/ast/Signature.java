package edu.utexas.mediator.ast;

import java.util.List;

public class Signature implements IVisitable {

  private String returnType;
  private String name;
  private List<String> argumentTypes;
  private List<String> arguments;

  public int getArgumentNum() {
    return arguments.size();
  }

  public String getReturnType() {
    return returnType;
  }

  public void setReturnType(String returnType) {
    this.returnType = returnType;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public List<String> getArgumentTypes() {
    return argumentTypes;
  }

  public void setArgumentTypes(List<String> argumentTypes) {
    this.argumentTypes = argumentTypes;
  }

  public List<String> getArguments() {
    return arguments;
  }

  public void setArguments(List<String> arguments) {
    this.arguments = arguments;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append(returnType).append(" ");
    builder.append(name).append("(");
    for (int i = 0; i < arguments.size(); ++i) {
      builder.append(argumentTypes.get(i)).append(" ").append(arguments.get(i)).append(", ");
    }
    if (arguments.size() > 0) builder.delete(builder.length() - 2, builder.length());
    builder.append(")");
    return builder.toString();
  }

  @Override
  public int hashCode() {
    return (returnType.hashCode() ^ name.hashCode())
        + (argumentTypes.hashCode() ^ arguments.hashCode());
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) return true;
    if (o instanceof Signature) {
      Signature obj = (Signature) o;
      return returnType.equals(obj.returnType) && name.equals(obj.name)
          && argumentTypes.equals(obj.argumentTypes) && arguments.equals(obj.arguments);
    }
    return false;
  }

  @Override
  public void accept(IVisitor visitor) {
    visitor.visit(this);
  }

}
