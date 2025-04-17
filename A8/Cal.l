%{
#include <stdlib.h>
#include <string.h>
#include "Cal.tab.h"
void yyerror(char *error);
%}

%%

[0-9]+ {
    yylval.intval = atoi(yytext);
    return NUMBER;
}

[a-z]+ {
    yylval.fchar = strdup(yytext);
    return NAME;
}

[\t ] ;  // skip whitespace

\n return 0;

. { return yytext[0]; }

%%

char* strdup(const char* s) {
    char* copy = (char*)malloc(strlen(s) + 1);
    if (copy) {
        strcpy(copy, s);
    }
    return copy;
}

int yywrap() {
    return 1;
}
