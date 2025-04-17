%{
#include "one.tab.h"
extern int yyerror(char *str);
extern int yyparse();
%}

%%

"if"        { return IF; }
"while"     { return WH; }
"("         { return OP; }
")"         { return CP; }
"{"         { return CCP; }
"}"         { return OCP; }
"<" |
">" |
"<=" |
">=" |
"==" |
"!="        { return CMP; }
"*" |
"/" |
"-" |
"+"         { return OPR; }
"="         { return ASG; }
([a-zA-Z_][a-zA-Z0-9_]*) { return ID; }
[0-9]+      { return NUM; }
";"         { return SC; }
[ \t\r\n]+  ; // skip whitespace

%%

int yywrap()
{
    return 1;
}
