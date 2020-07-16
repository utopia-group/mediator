grammar AntlrDsl;

@header {
package edu.utexas.mediator.parser;
}

MEM :   'in' ;
LNOT:   'not' ;
LAND:   'and' ;
LOR :   'or' ;
JOIN:   'join' ;
SEL :   'sigma' ;
PROJ:   'pi' ;
CUP :   'cup' ;
DIFF:   'minus' ;
INS :   'ins' ;
DEL :   'del' ;
UPD :   'upd' ;

LOP :   '=' | '!=' | '<' | '<=' | '>' | '>=' ;
ID  :   [a-zA-Z_][a-zA-Z0-9._-]* ;
WS  :   [ \t\n\r]+ -> skip ;


stmtRoot
    :   stmt EOF
    ;

stmt
    :   queryExpr  # ToQueryExpr
    |   insStmt    # ToInsStmt
    |   delStmt    # ToDelStmt
    |   updStmt    # ToUpdStmt
    ;

queryExpr
    :   JOIN '(' queryExpr ',' queryExpr ')'  # JoinExpr
    |   SEL '(' pred ',' queryExpr ')'        # SelExpr
    |   PROJ '(' attrList ',' queryExpr ')'   # ProjExpr
    |   CUP '(' queryExpr ',' queryExpr ')'   # UnionExpr
    |   DIFF '(' queryExpr ',' queryExpr ')'  # MinusExpr
    |   ID                                    # ToId
    ;

insStmt
    :   INS '(' ID ',' tuple ')'
    ;

delStmt
    :   DEL '(' ID ',' pred ')'
    ;

updStmt
    :   UPD '(' ID ',' pred ',' ID ',' ID ')'
    ;

attrList
    :   '[' ID (',' ID)* ']'
    ;

tuple
    :   '(' ID (',' ID)* ')'
    ;

pred
    :   LNOT '(' pred ')'             # NotPred
    |   LAND '(' pred ',' pred ')'    # AndPred
    |   LOR '(' pred ',' pred ')'     # OrPred
    |   MEM '(' ID ',' queryExpr ')'  # InPred
    |   ID LOP ID                     # LopPred
    ;

