%{
#include <stdlib.h>
#include <stdio.h>
#include <math.h>
int yylex(void);               
void yyerror(const char *); 
%}

%union{
char* fchar;
double fval;
int intval;
};

%token <fchar>NAME
%token <intval>NUMBER
%type <fval>exp
%left '+' '-'
%left '*' '/'

%%

stmt: NAME'='exp { printf("=%f\t", $3); printf("\nCalled stmt -> name = exp\n"); }

| exp { printf("=%f\n", $1); }
;

exp: exp '+' exp {$$ = $1 + $3; printf("Called exp -> exp + exp\n"); }
| exp '-' exp {$$ = $1 - $3; printf("Called exp -> exp - exp\n");}
| exp '*' exp {$$ = $1 * $3; printf("Called exp -> exp * exp\n");}
| exp '/' exp {
if ($3==0)
{
printf("Divide by zero\n");
}
else
{
$$ = $1 / $3;
printf("Called exp -> exp / exp\n");
}
}
| NUMBER {$$ = $1; printf("Called exp -> Number\n");}
;

%%

void yyerror(const char *error)
{
printf("%s", error);
}

int main()
{

yyparse();
return 0;
}