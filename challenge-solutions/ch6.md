# 1.
> In C, a block is a statement form that allows you to pack a series of statements where a single one is expected. The comma operator is an analogous syntax for expressions. A comma-separated series of expressions can be given where a single expression is expected (except inside a function call’s argument list). At runtime, the comma operator evaluates the left operand and discards the result. Then it evaluates and returns the right operand.
>
> Add support for comma expressions. Give them the same precedence and associativity as in C. Write the grammar, and then implement the necessary parsing code.

The comma operator has a simple rule like the others in the chapter. In C only the semicolon has higher precedence.
```
expression   ->   comma ;

comma        ->   equality ("," equality)* ;

equality     ->   ...
```

Its implementation simply follows the same pattern as the other expression types, so I omit it here.
I was wondering about the logic to only return the right operand, but that's done at runtime / has to do with evaluation, which is an interpreter concept, not a parser one.

# 2.
The ternary rule is inserted as follows:

```
expression   ->   comma ;

comma        ->   ternary ("," ternary)* ;

ternary      ->   equality ("?" ternary ":" ternary)? ;

equality     ->   ...
```

Precedence: the precedence is low, just higher than the comma operator in C. Confused by question wording (postscript: I don't understand solution, operands don't have precedence. is he talking about evaluation order? not sure).

Associativity: the operator should be right-associative since it's more intuitive for the programmer, e.g. `a == 1 ? "one" : (a == 2 ? "two" : "many")`


# 3.
```
expression     → equality ;
equality       → comparison ( ( "!=" | "==" ) comparison )* ;
comparison     → term ( ( ">" | ">=" | "<" | "<=" ) term )* ;
term           → factor ( ( "-" | "+" ) factor )* ;
factor         → unary ( ( "/" | "*" ) unary )* ;
unary          → ( "!" | "-" ) unary
               | primary ;
primary        → NUMBER | STRING | "true" | "false" | "nil"
               | "(" expression ")"
               // error productions
               | ( "!=" | "==" ) equality
               | ( ">" | ">=" | "<" | "<=" ) comparison
               | ( "+" ) term
               | ( "/" | "*" ) factor ;
```

thoughts:
- put this is primary? errors will always fall all the way down to the bottom (unless triggered by a
`consume`). the alternative is to add error handling in each of the rules for precedence, but that spreads one job across many rules, i.e. poor encapsulation.
- my only doubt is that I don't see why every error production would just go in the bottom rule.
intuitively, you must rule out every possible match before erroring, which supports this.