grammar TILScript;

@header {
package org.fpeterek.til.parser;
}

start : sentence*;

sentence : sentenceContent terminator;

sentenceContent : typeDefinition
                | entityDefinition
                | construction
                | globalVarDecl
                | globalVarDef
                | funDefinition;

terminator : TERMINATOR;

typeDefinition : TYPEDEF WS typeName ASSIGN dataType;

funDefinition : DEFN WS entityName OPEN_PAR typedVariables CLOSE_PAR ARROW typeName ASSIGN construction;

entityDefinition : entityName (COMMA entityName)* FS dataType;

construction : (trivialization | variable | closure | nExecution | composition) WT?;

globalVarDecl : LET variableName (COMMA variableName)* ARROW dataType;

globalVarDef : LET variableName ARROW dataType ASSIGN construction;

dataType : (builtinType | listType | tupleType | userType | compoundType) TW?;

builtinType : BUILTIN_TYPE | ASTERISK;

listType : LIST LESS dataType GREATER;

tupleType : TUPLE LESS dataType GREATER;

userType : typeName;

compoundType : OPEN_PAR dataType (WS dataType)* CLOSE_PAR;

variable : variableName;

trivialization : TRIVIALIZE (construction | entity );

composition : OPEN_BRA construction (construction | (WS construction))+ CLOSE_BRA;

closure : OPEN_BRA lambdaVariables (construction | (WS construction)) CLOSE_BRA;

lambdaVariables : LAMBDA optTypedVariables;

nExecution : EXEC (construction | entity);

optTypedVariables : optTypedVariable (COMMA optTypedVariable)*;

typedVariables : typedVariable (COMMA typedVariable)*;

optTypedVariable : variableName (COLON typeName)?;

typedVariable : variableName COLON typeName;

entity : keyword | entityName | number | symbol;

typeName : ucname;

entityName : ucname;

variableName : lcname;

keyword : 'True'
        | 'False'
        | 'If'
        | 'Tr'
        | 'Improper'
        | 'Nil';


symbol : SYMBOLS | ASTERISK | FS;

number : NUMBER;

ucname : UCNAME;
lcname : LCNAME;

NUMBER : DIGIT DIGIT*
       | DIGIT DIGIT* '.' DIGIT DIGIT*;

UCNAME : [A-Z] ([A-Za-z_0-9ěščřýáďéíňóúůťžĚŠČŘÝÁĎÉÍŇÓÚŮŤŽ])*;
LCNAME : [a-z] ([A-Za-z_0-9ěščřýáďéíňóúůťžĚŠČŘÝÁĎÉÍŇÓÚŮŤŽ])*;

DIGIT   : [0-9];
ZERO    : '0';
NONZERO : [1-9];


BUILTIN_TYPE : 'Bool'
             | 'Indiv'
             | 'Time'
             | 'String'
             | 'World'
             | 'Real'
             | 'Int'
             | ANY;

SYMBOLS : ('+' | '-' | '=');

ANY : 'Any' DIGIT*;

EXEC       : '^' NONZERO OPT_WS;
LAMBDA     : '\\' OPT_WS;
TRIVIALIZE : '\'' OPT_WS;
OPEN_BRA   : '[' OPT_WS;
CLOSE_BRA  : OPT_WS ']';
OPEN_PAR   : OPT_WS '(' OPT_WS;
CLOSE_PAR  : OPT_WS ')' OPT_WS;
LESS       : OPT_WS '<' OPT_WS;
GREATER    : OPT_WS '>' OPT_WS;
ARROW      : OPT_WS '->' OPT_WS;
TERMINATOR : OPT_WS '.' OPT_WS;
ASSIGN     : OPT_WS '=' OPT_WS;
COMMA      : OPT_WS ',' OPT_WS;
COLON      : OPT_WS ':' OPT_WS;
FS         : OPT_WS '/' OPT_WS;
ASTERISK   : '*';
TYPEDEF    : 'typedef';
LIST       : 'List';
TUPLE      : 'Tuple';
DEFN       : 'defn';
LET        : 'let';

WT : OPT_WS '@wt';
TW : OPT_WS '@tw';

WS              : WS_CHARS+;
fragment OPT_WS : WS_CHARS*;

WS_CHARS : ('\r' | '\n' | '\t' | ' ');
