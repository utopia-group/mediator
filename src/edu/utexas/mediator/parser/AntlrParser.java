package edu.utexas.mediator.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

import edu.utexas.mediator.ast.AstNode;
import edu.utexas.mediator.ast.AttrDecl;
import edu.utexas.mediator.ast.Program;
import edu.utexas.mediator.ast.RelDecl;
import edu.utexas.mediator.ast.Schema;
import edu.utexas.mediator.ast.Signature;
import edu.utexas.mediator.ast.Transaction;
import edu.utexas.mediator.bench.Prog;
import edu.utexas.mediator.bench.Tran;

public class AntlrParser implements IParser {

  @Override
  public Program parse(Prog prog) {
    Schema schema = parseSchema(prog.getRelations(), prog.getPrimarykeys(), prog.getForeignkeys());
    List<Transaction> transactionList = new ArrayList<>();
    for (Tran tran : prog.getTransactions()) {
      Signature signature = parseSignature(tran.getSignature());
      List<AstNode> statementList = new ArrayList<>();
      for (String stmt : tran.getBody()) {
        statementList.add(parseStatement(stmt, schema));
      }
      transactionList.add(new Transaction(signature, statementList));
    }
    return new Program(schema, transactionList);
  }

  public AstNode parseStatement(String stmt, Schema schema) {
    ANTLRInputStream inputStream = new ANTLRInputStream(stmt);
    AntlrDslLexer lexer = new AntlrDslLexer(inputStream);
    CommonTokenStream tokens = new CommonTokenStream(lexer);
    AntlrDslParser parser = new AntlrDslParser(tokens);
    ParseTree tree = parser.stmtRoot();
    AntlrAstGenVisitor astGenVisitor = new AntlrAstGenVisitor(schema);
    AstNode ast = astGenVisitor.visit(tree);
    return ast;
  }

  public Signature parseSignature(String input) {
    Signature signature = new Signature();
    Pattern pattern = Pattern.compile("(.*?)\\s+([a-zA-Z_][a-zA-Z0-9_-]*)\\((.*)\\)");
    Matcher matcher = pattern.matcher(input);
    if (matcher.find()) {
      signature.setReturnType(matcher.group(1));
      signature.setName(matcher.group(2));
      String argList = matcher.group(3);
      List<String> arguments = new ArrayList<>();
      List<String> argumentTypes = new ArrayList<>();
      if (!argList.isEmpty()) {
        String[] chunks = argList.split("\\s*,\\s*");
        for (String chunk : chunks) {
          String[] tokens = chunk.split("\\s+");
          assert tokens.length == 2;
          argumentTypes.add(tokens[0]);
          arguments.add(tokens[1]);
        }
      }
      signature.setArguments(arguments);
      signature.setArgumentTypes(argumentTypes);
    } else {
      throw new RuntimeException("Parsing error in signature: " + input);
    }
    return signature;
  }

  public Schema parseSchema(List<String> relations, List<String> pks, List<String> fks) {
    Schema schema = parseSchemaRelations(relations);
    parseSchemaPKs(schema, pks);
    parseSchemaFKs(schema, fks);
    return schema;
  }

  private Schema parseSchemaRelations(List<String> relations) {
    List<RelDecl> relDecls = new ArrayList<>();
    Pattern pattern = Pattern.compile("([a-zA-Z_][a-zA-Z0-9._-]*)\\((.*)\\)");
    for (String input : relations) {
      Matcher matcher = pattern.matcher(input);
      if (matcher.find()) {
        RelDecl relDecl = new RelDecl();
        relDecl.setName(matcher.group(1));
        List<AttrDecl> attrDecls = new ArrayList<>();
        String attrList = matcher.group(2);
        if (!attrList.isEmpty()) {
          String[] chunks = attrList.split("\\s*,\\s*");
          for (String chunk : chunks) {
            String[] tokens = chunk.split("\\s+");
            assert tokens.length == 2;
            AttrDecl attrDecl = new AttrDecl(tokens[0], tokens[1]);
            attrDecl.setRelDecl(relDecl);
            attrDecls.add(attrDecl);
          }
        }
        relDecl.setAttrDecls(attrDecls);
        relDecls.add(relDecl);
      } else {
        throw new RuntimeException("Parsing error in relations: " + input);
      }
    }
    return new Schema(relDecls);
  }

  private void parseSchemaPKs(Schema schema, List<String> pks) {
    String idRegex = "[a-zA-Z_][a-zA-Z0-9._-]*";
    Pattern pattern = Pattern.compile("(" + idRegex + ")\\(\\s*(" + idRegex + ")\\s*\\)");
    for (String pk : pks) {
      Matcher matcher = pattern.matcher(pk);
      if (matcher.find()) {
        String relName = matcher.group(1);
        String attrName = matcher.group(2);
        RelDecl relDecl = schema.getRelDeclByName(relName);
        AttrDecl attrDecl = relDecl.getAttrDeclByName(attrName);
        attrDecl.setPrimaryKey(true);
      } else {
        throw new RuntimeException("Parsing error in primary keys: " + pk);
      }
    }
  }

  private void parseSchemaFKs(Schema schema, List<String> fks) {
    String idRegex = "[a-zA-Z_][a-zA-Z0-9._-]*";
    String keyRegex = "(" + idRegex + ")\\(\\s*(" + idRegex + ")\\s*\\)";
    Pattern pattern = Pattern.compile(keyRegex + "\\s*->\\s*" + keyRegex);
    for (String fk : fks) {
      Matcher matcher = pattern.matcher(fk);
      if (matcher.find()) {
        String relName = matcher.group(1);
        String attrName = matcher.group(2);
        String refRelName = matcher.group(3);
        String refAttrName = matcher.group(4);
        AttrDecl attrDecl = schema.getRelDeclByName(relName).getAttrDeclByName(attrName);
        AttrDecl refAttrDecl = schema.getRelDeclByName(refRelName).getAttrDeclByName(refAttrName);
        attrDecl.setForeignKey(true);
        attrDecl.setReferenceAttrDecl(refAttrDecl);
      } else {
        throw new RuntimeException("Parsing error in foreign keys: " + fk);
      }
    }
  }

}
