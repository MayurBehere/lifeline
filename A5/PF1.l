%{
 
%}
L [a-z]
D [0-9]
O "+"|"*"|"-"
%%

int|float|char|string|double { printf("this is datatype %s",yytext);}
if|while|do|for|then|switch|case { printf("%s this is loop keyword",yytext);}
{D}+ { printf("this is number %s",yytext);}
{L}({L}|{D})*   { printf("this is identifier %s",yytext);}
{O} { printf("this is operator %s",yytext);}
{D}({O}|{D})* { printf("this is expression %s",yytext);}
[{D}] { printf("this is array %s",yytext);}
{L}+{D}({O}|{D}) { printf("this is assignment %s",yytext);}
= { printf("this is array %s",yytext);}
{L}({L}|{D})*"["{D}*"]" { printf("this is array %s",yytext);}
%%

int main()
{
    yyin=fopen("input1.c","r");
    // yyout=fopen("output1.c","w+");
    yylex();
    yywrap();
    return 0;
}

int yywrap()
{
    return 1;
}

