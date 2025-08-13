# 1.
> A few chapters from now, when Lox supports first-class functions and dynamic dispatch, we technically won’t need branching statements built into the language. Show how conditional execution can be implemented in terms of those. Name a language that uses this technique for its control flow.

This was a bit of a stumper. But it makes a lot of sense. From sol: "The basic idea is that the control flow operations become methods that take callbacks for the blocks to execute when true or false". Smalltalk is implemented like this. Cool!

Solution example:
```
// declarations
class True {
  ifThen(thenBranch) {
    return thenBranch();
  }

  ifThenElse(thenBranch, elseBranch) {
    return thenBranch();
  }
}

class False {
  ifThen(thenBranch) {
    return nil;
  }

  ifThenElse(thenBranch, elseBranch) {
    return elseBranch();
  }
}

// declare as singletons
var t = True();
var f = False();

// usage
fun test(condition) {
  fun ifThenFn() {
    print "if then -> then";
  }

  condition.ifThen(ifThenFn);

  fun ifThenElseThenFn() {
    print "if then else -> then";
  }

  fun ifThenElseElseFn() {
    print "if then else -> else";
  }

  condition.ifThenElse(ifThenElseThenFn, ifThenElseElseFn);
}

test(t);
test(f);
```

# 2.
> Likewise, looping can be implemented using those same tools, provided our interpreter supports an important optimization. What is it, and why is it necessary? Name a language that uses this technique for iteration.

Scheme! I heard about this from SICP. The insight is that functions of the form:
```
fun f (...) {
    // do stuff...
    // ...
    f(...);
}
```
are special because since the last statement is the recursive call. if there were code after it, you'd need a way to pause and "save your progress" in the current functions's execution, and then go do the recursive call to f, and then resume where you left off. The classic solution to this is the call stack. However, when the recursive call (or any function call, actually) is the last statement, there is no "progress" to "save"! You've done it all already! So you can scrap everything and have the new function your calling return directly to the current caller. So it's recursion without the memory overhead of the growing call stack. It's called "tail recursion". This is an optimization you'd make in the compiler (which I assume amounts to checking for the described scenario directly). From this you can directly implement looping constructs / syntax sugar.

# 3.
> Unlike Lox, most other C-style languages also support break and continue statements inside loops. Add support for break statements.
>
> The syntax is a break keyword followed by a semicolon. It should be a syntax error to have a break statement appear outside of any enclosing loop. At runtime, a break statement causes execution to jump to the end of the nearest enclosing loop and proceeds from there. Note that the break may be nested inside other blocks and if statements that also need to be exited.

This seems quite doable using exceptions in Java. We'll wrap each loop with a try/catch for a special "break" exception. easy peasy.

Edit: doh! Haha. I got it working cleanly, then checked the solution. The issue is, a dangling break is a runtime error in my implementation, when it should be a syntax (parse-time) error! Wow, what a elucidating moment. Of course. I was checking at the top level for an uncaught "break exception" (was going to check on function boundaries once functions implemented). The static way to do it is to keep track of loop depth in the parser. Still easy.