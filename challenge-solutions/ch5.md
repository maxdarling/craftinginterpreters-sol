# 1.
> Earlier, I said that the |, *, and + forms we added to our grammar metasyntax were just syntactic sugar. Take this grammar:
> ```
> expr → expr ( "(" ( expr ( "," expr )* )? ")" | "." IDENTIFIER )+
>      | IDENTIFIER
>      | NUMBER
> ```
> Produce a grammar that matches the same language but does not use any of that notational sugar.
> *Bonus: What kind of expression does this bit of grammar encode?*

expr -> NUMBER
expr -> IDENTIFIER

expr -> expr "." IDENTIFIER

expr -> expr "(" args ")"
args -> ""
args -> expr
args -> args "," expr

The above encodes function calls and property access. E.g. `foo(bar, baz(a, b.num, c + 1), d)`.

# 2.
> The Visitor pattern lets you emulate the functional style in an object-oriented language. Devise a complementary pattern for a functional language. It should let you bundle all of the operations on one type together and let you define new types easily.
>
> (SML or Haskell would be ideal for this exercise, but Scheme or another Lisp works as well.)

This is covered in-depth in section 2.4.3 of SICP (2nd ed.), which I did ~1 year ago and loved.

You'd use message passing. See below for 2 styles of implementation of a complex number object with generic operations.

Note: these are pedagogical examples - I'm not sure what would be used in practice. There's a third option, too, which is "data-directed programming", covered at the start of section 2.4.3, which seems really spiffy.


## traditional style: dispatch on type ("intelligent operations")
```scheme
;; rectangular representation
(define (real-part-rectangular z) (car z))
(define (imag-part-rectangular z) (cdr z))
(define (magnitude-rectangular z)
    (sqrt (+ (square (real-part-rectangular z))
    (square (imag-part-rectangular z)))))
(define (angle-rectangular z)
    (atan (imag-part-rectangular z)
    (real-part-rectangular z)))
(define (make-from-real-imag-rectangular x y)
    (attach-tag 'rectangular (cons x y)))
(define (make-from-mag-ang-rectangular r a)
    (attach-tag 'rectangular
                (cons (* r (cos a)) (* r (sin a)))))

;; < polar representation omitted>

;; < typing utils omitted (attach-tag, type-tag, contents, rectangular?, polar?) >

;; generic operation definitions
(define (real-part z)
    (cond ((rectangular? z)
           (real-part-rectangular (contents z)))
          ((polar? z)
           (real-part-polar (contents z)))
          (else (error "Unknown type: REAL-PART" z))))
;; ...
;; ... and so on. each function dispatches on type
```

## message passing style: dispatch on operation names ("intelligent data")
```scheme
;; rectangular representation
(define (make-from-real-imag x y)
    (define (dispatch op)
        (cond ((eq? op 'real-part) x)
            ((eq? op 'imag-part) y)
            ((eq? op 'magnitude) (sqrt (+ (square x) (square y))))
            ((eq? op 'angle) (atan y x))
            (else (error "Unknown op: MAKE-FROM-REAL-IMAG" op))))
    dispatch)

;; < polar representation omitted >

;; generic operation definitions
(define (real-part z) (apply-generic 'real-part z))
(define (imag-part z) (apply-generic 'imag-part z))
(define (magnitude z) (apply-generic 'magnitude z))
(define (angle z) (apply-generic 'angle z))

(define (apply-generic op arg) (arg op))
```

# 3.
> In reverse Polish notation (RPN), the operands to an arithmetic operator are both placed before the operator, so 1 + 2 becomes 1 2 +. Evaluation proceeds from left to right. Numbers are pushed onto an implicit stack. An arithmetic operator pops the top two numbers, performs the operation, and pushes the result. Thus, this:
>
> ```
> (1 + 2) * (4 - 3)
> ```
> in RPN becomes:
>
> ```
> 1 2 + 4 3 - *
> ```
> Define a visitor class for our syntax tree classes that takes an expression, converts it to RPN, and returns the resulting string.

You can just slightly modify the the `parenthesize` method to not include the parens and swap the order.

```java
private String toRPN(String name, Expr... exprs) {
  StringBuilder builder = new StringBuilder();

  for (Expr expr : exprs) {
    builder.append(expr.accept(this));
    builder.append(" ");
  }
  builder.append(name);

  return builder.toString();
}
```