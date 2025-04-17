#include<stdio.h>
#include<stdlib.h>

char str[100];
int i = 0;
char tp;

void advance() {
    i++;
    tp = str[i];
}

void E() {
    T();
    EP();
}

void EP() {
    if (tp == '+') {
        advance();
        T();
        EP();
    }
}

void T() {
    F();
    TP();
}

void TP() {
    if (tp == '*') {
        advance();
        F();
        TP();
    }
}

void F() {
    if (tp == 'i' || tp == 'd') {
        advance();
    } else if (tp >= '0' && tp <= '9') {
        advance();
    } else {
        if (tp == '(') {
            advance();
            E();
            if (tp == ')') {
                advance();
            } else {
                printf("String not accepted: Missing closing parenthesis\n");
                exit(1);
            }
        } else {
            printf("String not accepted: Invalid character\n");
            exit(1);
        }
    }
}

int main() {
    int op;
    while (1) {
        printf("Enter the string: ");
        scanf("%s", str);
        i = 0;
        tp = str[i];
        E();

        if (tp == '\0') {
            printf("String is accepted\n");
        } else {
            printf("String is rejected\n");
        }

        printf("Type 1 to exit, any other key to continue: ");
        scanf("%d", &op);
        if (op == 1) {
            exit(0);
        }
    }
}
