package com.craftinginterpreters.lox;

import java.util.List;

class LoxFunction implements LoxCallable {
  private final String name;
  private final Expr.Function declaration;
  private final Environment closure;

  private final boolean isInitializer;


  LoxFunction(String name, Expr.Function declaration, Environment closure, boolean isInitializer) {
    this.name = name;
    this.closure = closure;
    this.declaration = declaration;
    this.isInitializer = isInitializer;
  }

  LoxFunction bind(LoxInstance instance) {
    Environment environment = new Environment(closure);
    environment.define(instance); // define 'this'
    return new LoxFunction(name, declaration, environment, isInitializer);
  }

  @Override
  public String toString() {
    // do other langs use a '#', or am I tripping? '@' is also used, for mem loc.?
    if (name == null) return "<lambda#" + this.hashCode() + ">";
    return "<fn " + name + ">";
  }

  @Override
  public int arity() {
    return declaration.params.size();
  }

  @Override
  public Object call(Interpreter interpreter, List<Object> arguments) {
    Environment environment = new Environment(closure);
    for (int i = 0; i < declaration.params.size(); i++) {
      environment.define(arguments.get(i));
    }

    try {
      interpreter.executeBlock(declaration.body, environment);
    } catch (Return returnValue) {
      // note: this and below are slightly tricky
      // 1. the env is 'closure', not 'environment', because of how we handle bound methods (see LoxInstance.get()).
      //    each method handle is wrapped in an env with just 'this' bound. see book 12.6 for a great explanation.
      // 2. the slot is 0 because it's the only name bound in said env
      if (isInitializer) return closure.getAt(0, 0);

      return returnValue.value;
    }

    if (isInitializer) return closure.getAt(0, 0);
    return null;
  }
}
