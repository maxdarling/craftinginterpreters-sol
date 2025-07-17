# 1
> Allowing comparisons on types other than numbers could be useful. The operators might have a reasonable interpretation for strings. Even comparisons among mixed types, like 3 < "pancake" could be handy to enable things like ordered collections of heterogeneous types. Or it could simply lead to bugs and confusion.

A classic is string-string comparison, ordered lexicoographically. This is second nature for me as a programmer. And virtually all languages support it, upon light research (exceptions: C since String doesn't exist, Java uses `compareTo()` but not `<`).

As for multi-type comparison as in the example, JS and PHP notably allow this. Ruby, Lua, Python3 don't. Note Python2 did - they probably learned from hindsight.

# 2
> Many languages define + such that if either operand is a string, the other is converted to a string and the results are then concatenated. For example, "scone" + 4 would yield scone4. Extend the code in visitBinaryExpr() to support that.

All Java objects are derived from `Object`, which has a default `toString()`. And the Java types we use - `Double`, `Boolean` - implement it appropriately. So this is easy to do. Commentary: that's good design! So simple and convenient. I know that C++ doesn't support this, e.g. you can't `<<` a `vector<int>` by default, you have to write your own or use `fmt` from C++20. Interesting. The "zero-cost abstractions" philosophy.

# 3
> What happens right now if you divide a number by zero? What do you think should happen? Justify your choice. How do other languages you know handle division by zero, and why do they make the choices they do?
>
> Change the implementation in visitBinaryExpr() to detect and report a runtime error for this case.

Currently `x / 0` in Lox maps to `x / 0.0` in Java, which is `Infinity` or `NaN` if x = 0. `0.0 / 0` also yields `NaN` in Java, but any other `x / 0` yields an ArithmeticException. Funky.

My first idea is that 0.0 is what an infinitessimally small fraction approaches, and division by such logically yields infinity. Whereas int division by 0 is undefined. But this doesn't explain the `0.0 / 0` case. I'd guess that for mixed int and double expressions the int always gets casted to a double first or something, idk.

Edit: after research, it looks like it's due to the IEEE 754 standard for floating point division. Interesting. And it seems like scientific computing would encounter crashes constantly if not for the special values NaN and Infinity (according to ChatGPT). I guess that makes sense.

The several other popular languages I looked at handle div by 0 the same, except python and JS. Lol. The peanut gallery. But I guess this behavior can be argued for, for high-level scripting langs that don't expect to do sci computing. Note that for scientific libs in Python, e.g. Pandas, would swallow exceptions and delivery expected behavior.