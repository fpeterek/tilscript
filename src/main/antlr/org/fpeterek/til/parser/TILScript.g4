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

entityDefinition : entityName (COMMA entityName)* SLASH dataType;

construction : (trivialization | variable | closure | nExecution | composition) WT?;

globalVarDef : variableName (COMMA variableName) ARROW dataType;


dataType : (builtinType | listType | tupleType | userType | compoundType) TW?;

builtinType : 'Bool'
            | 'Indiv'
            | 'Time'
            | 'String'
            | 'World'
            | 'Real'
            | 'Int'
            | any
            | '*';

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

any : 'Any' DIGIT*;

symbol : SYMBOLS;

number : DIGIT DIGIT*
       | DIGIT DIGIT* '.' DIGIT DIGIT*;

ucname : UPPERCASE (LOWERCASE | UPPERCASE | '_' | DIGIT)*;
lcname : LOWERCASE (LOWERCASE | UPPERCASE | '_' | DIGIT)*;

DIGIT : (ZERO | NONZERO);
ZERO : '0';
NONZERO : [1-9];

LOWERCASE : [a-zěščřýáďéíňóúůťž];

UPPERCASE : [A-ZĚŠČŘÝÁĎÉÍŇÓÚŮŤŽ];

SYMBOLS : ('+' | '-' | '*' | '/' | '=');

EXEC : '^' NONZERO OPT_WS;
LAMBDA : '\\' OPT_WS;
TRIVIALIZE : '\'' OPT_WS;
OPEN_BRA : '[' OPT_WS;
CLOSE_BRA : OPT_WS ']';
OPEN_PAR : OPT_WS '(' OPT_WS;
CLOSE_PAR : OPT_WS ')';
ARROW : OPT_WS '->' OPT_WS;
TERMINATOR : OPT_WS '.' OPT_WS;
ASSIGNTYPE : OPT_WS ':=' OPT_WS;
COMMA: OPT_WS ',' OPT_WS;
COLON : OPT_WS ':' OPT_WS;
SLASH: OPT_WS '/' OPT_WS;

WT: '@wt';
TW: '@tw';

WS : WS_CHARS+;
fragment OPT_WS : WS_CHARS*;

WS_CHARS : ('\r' | '\n' | '\t' | ' ');
