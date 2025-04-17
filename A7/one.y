%{
#include <stdio.h>
extern int yylex();
extern int yywrap();
int yyerror(char *str); 
%}

%token IF OP CP CMP SC ASG ID NUM OPR CCP OCP WH

%%

start: sif
     | sw;

sif: IF OP cmpn CP stmt { printf("VALID STATEMENT IF\n"); };

sw: WH OP cmpn CP CCP wstmt OCP { printf("VALID STATEMENT WHILE\n"); };

cmpn: ID CMP ID  { printf("1\n"); } 
    | ID CMP NUM { printf("2\n"); };

stmt: ID ASG ID SC                 { printf("3\n"); }
    | ID ASG ID OPR NUM SC         { printf("4\n"); }
    | ID ASG NUM OPR ID SC         { printf("5\n"); }
    | ID ASG NUM OPR NUM SC        { printf("6\n"); }
    | ID ASG NUM SC                { printf("7\n"); }
    | ID ASG ID OPR ID SC          { printf("8\n"); };

wstmt: stmt;

%%

int yyerror (char *str)
{
    printf("syntax error\n");
    return 0;
}

int main()
{
    yyparse();
    return 1;
}
