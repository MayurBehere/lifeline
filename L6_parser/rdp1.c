#include <stdio.h>
#include <stdlib.h>
#include <string.h>

// Global variables
char str[100];
int i = 0;
char tp;

// Function declarations (prototypes)
void E();
void EP();
void T();
void TP();
void F();

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
    // ε-production: if no +, do nothing
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
    // ε-production: if no *, do nothing
}

void F() {
    if (tp == 'i') {
        advance();
    } else if (tp == '(') {
        advance();
        E();
        if (tp == ')') {
            advance();
        } else {
            printf("Error: Expected ')'\n");
            exit(1);
        }
    } else {
        printf("Error: Invalid symbol '%c'\n", tp);
        exit(1);
    }
}

int main() {
    printf("Enter the string: ");
    scanf("%s", str);

    // Initialize current token
    tp = str[i];

    E(); // Start parsing

    // If the whole string is consumed successfully
    if (tp == '\0') {
        printf("Valid string!\n");
    } else {
        printf("Invalid string! Unexpected character: '%c'\n", tp);
    }

    return 0;
}
