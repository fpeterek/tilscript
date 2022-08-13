grammar TILScript;

@header {
package org.fpeterek.til.parser;
}

start : sentence*;

sentence : sentenceContent terminator;

sentenceContent : globalVarDecl
                | globalVarDef
                | funDefinition
                | typeDefinition
                | entityDefinition
                | construction;

terminator : TERMINATOR;

typeDefinition : TYPEDEF WS typeName EQUAL_WS dataType;

funDefinition : DEFN WS entityName OPEN_PAR typedVariables CLOSE_PAR ARROW dataType EQUAL_WS construction;

entityDefinition : entityName (COMMA entityName)* FS_WITH_WS dataType;

construction : (trivialization | variable | closure | nExecution | composition | listInitializer) WT?;

listInitializer : OPEN_CUR construction (COMMA construction)* CLOSE_CUR;

globalVarDef : LET WS variableName COLON dataType EQUAL_WS construction;

globalVarDecl : DEF WS variableName (COMMA variableName)* COLON dataType;

dataType : (builtinType | listType | tupleType | userType | compoundType) TW?;

builtinType : BUILTIN_TYPE;

listType : LIST LESS dataType GREATER;

tupleType : TUPLE LESS dataType (COMMA dataType)* GREATER;

userType : typeName;

compoundType : OPEN_PAR dataType (WS dataType)* CLOSE_PAR;

variable : variableName;

trivialization : TRIVIALIZE (construction | entity | dataType);

composition : OPEN_BRA construction (construction | (WS construction))+ CLOSE_BRA;

closure : OPEN_BRA lambdaVariables (construction | (WS construction)) CLOSE_BRA;

lambdaVariables : LAMBDA optTypedVariables;

nExecution : EXEC (construction | entity);

optTypedVariables : optTypedVariable (COMMA optTypedVariable)*;

typedVariables : typedVariable (COMMA typedVariable)*;

optTypedVariable : variableName (COLON dataType)?;

typedVariable : variableName COLON dataType;

entity : entityName | number | symbol | string;

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
symbol : PLUS_WS | MINUS_WS | EQUAL_WS | ASTERISK_WS | FS_WITH_WS | LESS | GREATER;

number : NUMBER;

ucname : UCNAME;
lcname : LCNAME;

string: STRING;

STRING: '"([^\\"]|\\[^\n])*"';

LINE_COMMENT : '--' ~[\r\n]* -> channel(HIDDEN);

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

ANY : 'Any' LESS DIGIT* GREATER;

EXEC        : '^' NONZERO OPT_WS;
LAMBDA      : '\\' OPT_WS;
TRIVIALIZE  : '\'' OPT_WS;
OPEN_BRA    : '[' OPT_WS;
CLOSE_BRA   : OPT_WS ']';
OPEN_PAR    : OPT_WS '(' OPT_WS;
CLOSE_PAR   : OPT_WS ')' OPT_WS;
OPEN_CUR    : OPT_WS '{' OPT_WS;
CLOSE_CUR   : OPT_WS '}' OPT_WS;
LESS        : OPT_WS '<' OPT_WS;
GREATER     : OPT_WS '>' OPT_WS;
ARROW       : OPT_WS '->' OPT_WS;
TERMINATOR  : OPT_WS '.' OPT_WS;
COMMA       : OPT_WS ',' OPT_WS;
COLON       : OPT_WS ':' OPT_WS;
FS_WITH_WS  : OPT_WS '/' OPT_WS;
ASTERISK_WS : OPT_WS '*' OPT_WS;
PLUS_WS     : OPT_WS '+' OPT_WS;
MINUS_WS    : OPT_WS '-' OPT_WS;
EQUAL_WS    : OPT_WS '=' OPT_WS;
FS          : '/';
ASTERISK    : '*';
TYPEDEF     : 'typedef';
LIST        : 'List';
TUPLE       : 'Tuple';
DEFN        : 'defn';
DEF         : 'def';
LET         : 'let';

UCNAME : [A-Z] ([A-Za-z_0-9ěščřýáďéíňóúůťžĚŠČŘÝÁĎÉÍŇÓÚŮŤŽ])*;
LCNAME : [a-z] ([A-Za-z_0-9ěščřýáďéíňóúůťžĚŠČŘÝÁĎÉÍŇÓÚŮŤŽ])*;

WT : OPT_WS '@wt';
TW : OPT_WS '@tw';

WS              : WS_CHARS+;
fragment OPT_WS : WS_CHARS*;

WS_CHARS : ('\r' | '\n' | '\t' | ' ');
