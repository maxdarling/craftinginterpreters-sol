# 1. 
> The lexical grammars of Python and Haskell are not regular. What does that mean, and why aren’t they?

A regular language, among other things, can be represented by a regex. 
I know that one limitation of regexes is counting. Or any need for "memory" in general. I.e. you can't match strings of the form a^n b^n.

Using google, it's due to the languages enforcing indentation as part of syntax. I.e. they have to count the amount of indentation and compare it to adjacent lines, etc.
This is counting/memory, which you can't do.

# 2. 
> Aside from separating tokens—distinguishing print foo from printfoo—spaces aren’t used for much in most languages. However, in a couple of dark corners, a space does affect how code is parsed in CoffeeScript, Ruby, and the C preprocessor. Where and what effect does it have in each of those languages?

- Ruby: you can omit parens on method calls. E.g. `Array.new 1,2` == `Array.new(1,2)`
- CoffeeScript: same as above
- C preprocessor: Looked at solution. Below illustrates the difference between a function macro and a simple macro.

```
#define MACRO1 (p) (p)
#define MACRO2(p) (p)
```

# 3.
> Our scanner here, like most, discards comments and whitespace since those aren’t needed by the parser. Why might you want to write a scanner that does not discard those? What would it be useful for?

I was going to say doc comments, but that should be handled by a separate program that analyzes the source. Unless it was a built-in language feature.

# 4.
> Add support to Lox’s scanner for C-style /* ... */ block comments. Make sure to handle newlines in them. Consider allowing them to nest. Is adding support for nesting more work than you expected? Why?

```
- normal match process for a /* token. and then a terminating */ token. that seems pretty chill.
- nesting: instead of '/*'^n'*/', which is easy, you have to count the nesting level. not too hard. The solution links to an example from wren, and yes it's easy. Note that it makes the language non-regular, which could have... implications?