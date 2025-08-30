Chapter 4
- Scanner/Lexer -> lexical grammar, i.e. what arrangement of characters forms a valid word (token). think "lexicon". Parser -> syntactic grammar, i.e. what arrangement of words forms a valid sentence (expression). The former is "regular" (as in, regular languages) and the latter is "context free". See chomsky hierarchy: https://en.wikipedia.org/wiki/Chomsky_hierarchy

Chapter 5
- the "expression problem". really cool. such a pithy motivation for the object-oriented vs functional language debate.

Chapter 8
- "State and statements go hand in hand. Since statements, by definition, don’t evaluate to a value, they need to do something else to be useful. That something is called a side effect."
- 8.4: "Mutating a variable is a side effect and, as the name suggests, some language folks think side effects are dirty or inelegant. Code should be pure math that produces values—crystalline, unchanging ones—like an act of divine creation. Not some grubby automaton that beats blobs of data into shape, one imperative grunt at a time."
- The footnote at the top of 8.5 about Emacs lisp having dynamically-scoped variables brought me to an SO [thread](https://stackoverflow.com/questions/3786033/how-to-live-with-emacs-lisp-dynamic-scoping) with an elucidating comment from RMS:

> Some language designers believe that dynamic binding should be avoided, and explicit argument passing should be used instead. Imagine that function A binds the variable FOO, and calls the function B, which calls the function C, and C uses the value of FOO. Supposedly A should pass the value as an argument to B, which should pass it as an argument to C.
>
> This cannot be done in an extensible system, however, because the author of the system cannot know what all the parameters will be. Imagine that the functions A and C are part of a user extension, while B is part of the standard system. The variable FOO does not exist in the standard system; it is part of the extension. To use explicit argument passing would require adding a new argument to B, which means rewriting B and everything that calls B. In the most common case, B is the editor command dispatcher loop, which is called from an awful number of places.
>
> What's worse, C must also be passed an additional argument. B doesn't refer to C by name (C did not exist when B was written). It probably finds a pointer to C in the command dispatch table. This means that the same call which sometimes calls C might equally well call any editor command definition. So all the editing commands must be rewritten to accept and ignore the additional argument. By now, none of the original system is left!

Chapter 10
- 10.4: "Mechanically, the code is pretty simple. Walk a couple of lists. Bind some new variables. Call a method. But this is where the crystalline code of the function declaration becomes a living, breathing invocation. This is one of my favorite snippets in this entire book. Feel free to take a moment to meditate on it if you’re so inclined."