grammar Tilscript;

start : sentence;

sentence : ;

any : "Any" DIGIT*;

number : DIGIT DIGIT*
number | DIGIT DIGIT* "." DIGIT DIGIT*;

ucname : UPPERCASE (LOWERCASE | UPPERCASE | "_" | DIGIT)*;
lcname : LOWERCASE (LOWERCASE | UPPERCASE | "_" | DIGIT)*;

DIGIT : (ZERO | NONZERO);
ZERO : 0;
NONZERO : [1-9];

LOWERCASE : [a-zěščřýáďéíňóúůťž];

UPPERCASE : [A-ZĚŠČŘÝÁĎÉÍŇÓÚŮŤŽ];

SYMBOLS : [+-*/=];

OPT_WS : WS_CHARS?;
WS : WS_CHARS+;

WS_CHARS : [\s\r\n];
