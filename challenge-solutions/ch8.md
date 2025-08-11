# 1
> The REPL no longer supports entering a single expression and automatically printing its result value. That’s a drag. Add support to the REPL to let users type in both statements and expressions. If they enter a statement, execute it. If they enter an expression, evaluate it and display the result value.
I ended up looking at the solution and copying that. I thought of basically the same thing, but not as clean.

# 2.
> Maybe you want Lox to be a little more explicit about variable initialization. Instead of implicitly initializing variables to nil, make it a runtime error to access a variable that has not been initialized or assigned to, as in:
> ```
> // No initializers.
> var a;
> var b;
>
> a = "assigned";
> print a; // OK, was assigned first.
>
> print b; // Error!
> ```

I was going to pull all the functionality into the environment, which felt like "pulling complexity downwards", but it's not its responsibility. I was essentially going to have a defineUninit() alongside define() and store uninit-ed vars in a separate container until they were eventually assigned to or accessed erroneously. The book solution is nicer. I thought about the sentinel thing, but I couldn't come up with a good unique value not accessible in lox. I was thinking "hmm, perhaps any value is creatable in Lox" but the java equality operator on a static object is foolproof because it's checking program memory location.


# 3.
> What does the following program do?
> ```
> var a = 1;
> {
>   var a = a + 2;
>   print a;
> }
> ```
> What did you expect it to do? Is it what you think it should do? What does analogous code in other languages you are familiar with do? What do you think users will expect this to do?

It prints 3. When the assignment expression is parsed in the block, the rvalue is evaluated, and `a` is looked up and found in the enclosing context. It's then assigned to a new shadowed var `a` in the current environment and printed on the next line.

I'm biased because I'm working with Lox, but I think these semantics make sense. Should the rvalue or lvalue get evaluated first, that's the debate I supppose. Or, whether to allow shadowing at all. Or you could just call this kind of double reference erroneous, which I tihnk is fair. This is bad code because it's ambiguous and slightly tricky.