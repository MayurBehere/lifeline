%{
    #include "y.tab.h"
    extern int yyerror(char *str);
    extern int yyparse();
%}

%%
"if" return IF;
"(" return OP;
")" return CP;
"<" |
">" |
"<=" |
">=" |
"==" |
"!=" return CMP;
"while" return WH;
"do while" return DO;
"for" return FOR;
"{" return OCP;
"}" return CCB;
";" return SC;
"," return OPR;
"+" |
"-" |
"*" |
"/" return OPR;
"=" return ASG;
([a-zA-Z])("_"|[a-zA-Z0-9])* return ID;
[0-9] return NUM;
" " {}
%%

int yywrap(){
    return 1;
}