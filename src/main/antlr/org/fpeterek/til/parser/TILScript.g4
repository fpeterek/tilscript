grammar TILScript;

@header {
package org.fpeterek.til.parser;
}

start : sentence*;

sentence : sentenceContent terminator;

sentenceContent : typeDefinition
                | entityDefinition
                | construction
                | globalVarDef;

terminator : TERMINATOR;

typeDefinition : 'TypeDef' WS typeName ASSIGNTYPE dataType;

entityDefinition : entityName (COMMA entityName)* FS dataType;

construction : (trivialization | variable | closure | nExecution | composition) WT?;

globalVarDef : variableName (COMMA variableName)* ARROW dataType;


dataType : (builtinType | listType | tupleType | userType | compoundType) TW?;

builtinType : BUILTIN_TYPE | ASTERISK;
/*builtinType : 'Bool'
            | 'Indiv'
            | 'Time'
            | 'String'
            | 'World'
            | 'Real'
            | 'Int'
            | any
            | '*';*/

listType : 'List' OPEN_PAR dataType CLOSE_PAR;

tupleType : 'Tuple' OPEN_PAR dataType CLOSE_PAR;


userType : typeName;

compoundType : OPEN_PAR dataType (WS dataType)* CLOSE_PAR;

variable : variableName;

trivialization : TRIVIALIZE (construction | entity );

// TODO: Perhaps figure out a way to make WS optional in the following two
//       constructions
composition : OPEN_BRA construction (WS construction)+ CLOSE_BRA;

closure : OPEN_BRA lambdaVariables WS construction CLOSE_BRA;

lambdaVariables : LAMBDA typedVariables;

nExecution : EXEC (construction | entity);

typedVariables : typedVariable (COMMA typedVariable)*;

typedVariable : variableName (COLON typeName)?;

entity : keyword | entityName | number | symbol;

typeName : ucname;

entityName : ucname;

variableName : lcname;

keyword : 'ForAll'
        | 'Exist'
        | 'Every'
        | 'Some'
        | 'No'
        | 'True'
        | 'False'
        | 'And'
        | 'Or'
        | 'Not'
        | 'Implies'
        | 'Sing'
        | 'Sub'
        | 'Tr'
        | 'TrueC'
        | 'FalseC'
        | 'ImproperC'
        | 'TrueP'
        | 'FalseP'
        | 'UndefP'
        | 'ToInt';


symbol : SYMBOLS | ASTERISK | FS;

number : NUMBER;

NUMBER : DIGIT DIGIT*
        | DIGIT DIGIT* '.' DIGIT DIGIT*;

ucname : UCNAME;
lcname : LCNAME;

UCNAME : [A-Z] ([A-Za-z_0-9ěščřýáďéíňóúůťžĚŠČŘÝÁĎÉÍŇÓÚŮŤŽ])*;
LCNAME : [a-z] ([A-Za-z_0-9ěščřýáďéíňóúůťžĚŠČŘÝÁĎÉÍŇÓÚŮŤŽ])*;

DIGIT : [0-9];
ZERO : '0';
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

EXEC : '^' NONZERO OPT_WS;
LAMBDA : '\\' OPT_WS;
TRIVIALIZE : '\'' OPT_WS;
OPEN_BRA : '[' OPT_WS;
CLOSE_BRA : OPT_WS ']';
OPEN_PAR : OPT_WS '(' OPT_WS;
CLOSE_PAR : OPT_WS ')' OPT_WS;
ARROW : OPT_WS '->' OPT_WS;
TERMINATOR : OPT_WS '.' OPT_WS;
ASSIGNTYPE : OPT_WS ':=' OPT_WS;
COMMA: OPT_WS ',' OPT_WS;
COLON : OPT_WS ':' OPT_WS;
FS : OPT_WS '/' OPT_WS;
ASTERISK : '*';

WT: OPT_WS '@wt';
TW: OPT_WS '@tw';

WS : WS_CHARS+;
fragment OPT_WS : WS_CHARS*;

WS_CHARS : ('\r' | '\n' | '\t' | ' ');
