%{
    int i,m;
    struct symtab
    {
        char name[200];
        char type[200];
    }sym[20];
%}

L [a-z]
D [0-9]
O "+"|"*"|"-"
dt int|float|char|string|double
%%

{dt} { printf("this is datatype %s",yytext);}
if|while|do|for|then|switch|case { printf("%s this is loop keyword",yytext);}
{D}+ { printf("this is number %s",yytext);
                strcpy(sym[m].name,yytext);
                  strcpy(sym[m].type,"Arithmetic");
                  m++;
                }

{O} { printf("this is operator %s",yytext);}
{D}({O}|{D})* { printf("this is expression %s",yytext);}
[{D}] { printf("this is array %s",yytext);}
{L}+{D}({O}|{D}) { printf("this is assignment %s",yytext);}

{L}({L}|{D})*"["{D}*"]" { printf("this is array %s",yytext);
                            strcpy(sym[m].name,yytext);
                  strcpy(sym[m].type,"Array");
                  m++;
}

{L}({L}|{D})*   { printf("this is identifier %s",yytext);
                  strcpy(sym[m].name,yytext);
                  strcpy(sym[m].type,"Variable");
                m++;
                }
%%

int main()
{
    yyin=fopen("input1.c","r");
    // yyout=fopen("output1.c","w+");{L}({L}|{D})*   { printf("this is identifier %s",yytext);}
   
    yylex();
    yywrap();
    printf("The Symbol Table is \nid\tName \t Type");
     for(i=0;i<m;i++){
        fprintf(yyout,"\n%d",i+1);
        fprintf(yyout,"\t%s",sym[i].name);
        fprintf(yyout,"\t%s",sym[i].type);
    }
    return 0;
}

int yywrap()
{
    return 1;
}

