grammar TILScript;

@header {
package org.fpeterek.tilscript.parser;
}

start : sentence*;

sentence : sentenceContent terminator;

sentenceContent : globalVarDecl
                | globalVarDef
                | funDefinition
                | typeDefinition
                | entityDefinition
                | construction
                | importStatement
                | structDefinition;

structDefinition :
STRUCT entityName OPEN_CUR
    typedVariable
    (COMMA typedVariable)*
    COMMA? // Allow for trailing comma
CLOSE_CUR;

importStatement : IMPORT string;

terminator : TERMINATOR;

typeDefinition : TYPEDEF typeName ASSIGN dataType;

funDefinition : DEFN entityName OPEN_PAR typedVariables CLOSE_PAR ARROW dataType ASSIGN construction;

entityDefinition : entityName (COMMA entityName)* FS dataType;

nonNilConstruction : (trivialization | variable | closure | nExecution | composition | structConstructor) WT?;
construction : (nonNilConstruction | nil);

structConstructor : OPEN_CUR dataType construction* CLOSE_CUR;

globalVarDecl : variableName (COMMA variableName)* ARROW dataType;

globalVarDef : variableName ARROW dataType ASSIGN construction;

dataType : (builtinType | listType | tupleType | userType | compoundType) TW?;

builtinType : BUILTIN_TYPE;

listType : LIST OPEN_PAR dataType CLOSE_PAR;

tupleType : TUPLE OPEN_PAR dataType (COMMA dataType)* CLOSE_PAR;

userType : typeName;

compoundType : OPEN_PAR dataType (dataType)* CLOSE_PAR;

variable : variableName | structAttribute;

structAttribute : variableName DOUBLE_COLON variableName (DOUBLE_COLON variableName)*;

trivialization : TRIVIALIZE (nonNilConstruction | nonNilEntity | dataType);

composition : OPEN_BRA construction (construction)* CLOSE_BRA;

closure : OPEN_BRA lambdaVariables (ARROW dataType)? (construction) CLOSE_BRA;

lambdaVariables : LAMBDA typedVariables;

nExecution : EXEC (construction | entity);

typedVariables : typedVariable (COMMA typedVariable)*;

typedVariable : variableName COLON dataType;

entity : nonNilEntity | nil;
nonNilEntity : entityName | number | symbol | string;

typeName : ucname;

entityName : ucname;

variableName : lcname;

// For some reason, Antlr seems unable to match a single forward
// slash '/' and always matches the following space alongside the
// forward slash character
// The same behaviour does not occur with asterisk or other special
// symbols, no idea why
// Thus, I include optional whitespace as a part of the lexer rule
// and I'll have to trim the whitespace afterwards
symbol : PLUS | MINUS | EQUAL | ASTERISK | FS | LESS | GREATER;

number : NUMBER;

ucname : UCNAME;
lcname : LCNAME;

string: STRING_LITERAL;

nil : NIL;

// String literals were kindly borrowed from over here
// https://github.com/antlr/grammars-v4/blob/master/c/C.g4
// Because string literals are an absolute pain to deal with

STRING_LITERAL : '"' S_CHAR_SEQUENCE? '"';

fragment ESCAPE_SEQUENCE : SIMPLE_ESCAPE_SEQUENCE
                         | OCTAL_ESCAPE_SEQUENCE
                         | HEXADECIMAL_ESCAPE_SEQUENCE
                         | UNIVERSAL_CHARACTER_NAME;

fragment UNIVERSAL_CHARACTER_NAME : '\\u' HEX_QUAD
                                  | '\\U' HEX_QUAD HEX_QUAD;

fragment HEX_QUAD : HEXADECIMAL_DIGIT HEXADECIMAL_DIGIT HEXADECIMAL_DIGIT HEXADECIMAL_DIGIT;

fragment HEXADECIMAL_DIGIT : [0-9a-fA-F];

fragment OCTAL_DIGIT : [0-7];

fragment SIMPLE_ESCAPE_SEQUENCE : '\\' ['"?abfnrtv\\];

fragment OCTAL_ESCAPE_SEQUENCE : '\\' OCTAL_DIGIT OCTAL_DIGIT? OCTAL_DIGIT?;

fragment HEXADECIMAL_ESCAPE_SEQUENCE : '\\x' HEXADECIMAL_DIGIT+;

fragment S_CHAR_SEQUENCE : S_CHAR+;

fragment S_CHAR : ~["\\\r\n]
               | ESCAPE_SEQUENCE
               | '\\\n'
               | '\\\r\n';

NUMBER : DIGIT DIGIT*
       | DIGIT DIGIT* '.' DIGIT DIGIT*;

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
             | 'Construction'
             | ANY;

// TODO: Test
ANY : 'Any' (DIGIT | (NONZERO DIGIT+));

STRUCT       : 'Struct';
EXEC         : '^' [12];
LAMBDA       : '\\';
TRIVIALIZE   : '\'';
OPEN_BRA     : '[';
CLOSE_BRA    : ']';
OPEN_PAR     : '(';
CLOSE_PAR    : ')';
OPEN_CUR     : '{';
CLOSE_CUR    : '}';
LESS         : '<';
GREATER      : '>';
ARROW        : '->';
TERMINATOR   : '.';
COMMA        : ',';
DOUBLE_COLON : '::';
COLON        : ':';
PLUS         : '+';
MINUS        : '-';
ASSIGN       : ':=';
EQUAL        : '=';
FS           : '/';
ASTERISK     : '*';
TYPEDEF      : 'TypeDef';
LIST         : 'List';
TUPLE        : 'Tuple';
DEFN         : 'Defn';
LET          : 'Let';
IMPORT       : 'Import';
NIL          : 'Nil';

UCNAME : [A-Z] ([A-Za-z_0-9ěščřýáďéíňóúůťžĚŠČŘÝÁĎÉÍŇÓÚŮŤŽ])*;
LCNAME : [a-z] ([A-Za-z_0-9ěščřýáďéíňóúůťžĚŠČŘÝÁĎÉÍŇÓÚŮŤŽ])*;

WT : '@wt';
TW : '@tw';

LINE_COMMENT : '--' ~[\r\n]* -> skip;

WS_CHARS : ('\r' | '\n' | '\t' | ' ') -> skip;

//NEWLINE : ('\n' | '\r') -> skip;

