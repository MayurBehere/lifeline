%{
    #include<stdlib.h>
    #include"y.tab.h"
    #include<stdio.h>
    void yyerror(char *error);
%}

%%
[0-9]+ {yylval.intval=atoi(yytext);
return NUMBER;
}
"SIN" {return SIN;}
"COS" {return COS;}
"TAN" {return TAN;}
[a-z]+ {(yylval.fchar=*yytext);
return NAME;}
[\t] ;
\n return 0;
. {return yytext[0];}
%%

yywrap(){
    return 1;
}