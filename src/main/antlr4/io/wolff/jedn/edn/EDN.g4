/**
 * Define a grammar called Hello
 */
grammar EDN;

element : nil | bool | stringlit | charlit | symbol | keyword | floatlit | intlit | list | tag | discard | vector | map | set;

nil : NIL;

bool : BOOLEAN;

stringlit : STRING;

charlit : CHARLIT;

discard : '#_' element;

COMMENT : ';' .*? '\n' -> skip;

tag : '#' symbol WS element;

list : '(' element* ')';

vector : '[' element* ']';

map : '{' (element element)*'}';

set : '#{' element* '}';

floatlit : FLOAT;
intlit : INTEGER;

FLOAT: 
  INT ('M' | FRAC EXP? | EXP );
  
FRAC :
  '.' DIGIT+;
EXP : EX DIGIT+;

EX :  ('e' | 'E') [+-]?;

INTEGER : INT 'N'?;
INT: DIGIT
| [1-9] DIGIT*
| '+' [1-9] DIGIT*
| '-' DIGIT
| '-' [1-9] DIGIT*;

DIGIT : [0-9];

keyword : ':' symbol;

// TODO: implement real symbol rules
symbol : ALPHA+;

BEGINSPECIAL : [\-+.];

STRING :  '"' .*? '"';

CHARLIT : '\\' ( ALPHA | CHARNAME); // TODO unicode

ALPHA : [a-z] | [A-Z];

ALPHANUM : ALPHA | DIGIT;

SYMBOLCHAR : [.*+!\-_?$%&=<>]; // todo: sanitize


CHARNAME : 'newline' | 'return' | 'space' | 'tab';

WS : [ \t\r\n,]+ ; // skip spaces, tabs, newlines

NIL : 'nil';

BOOLEAN : 'true' | 'false';

