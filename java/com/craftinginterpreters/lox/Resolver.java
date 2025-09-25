package com.craftinginterpreters.lox;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

// the output of variable resolution. the Resolver "exports" one of these to the Interpreter for each var access.
//
// distance: # of scopes between this var access and the innermost scope
// slot      : position of variable's declaration in its scope. used for an optimization
//           (fast runtime lookups via array indexing rather than var name in map).
//
// note: this is a little tricky. but you should be able to picture (distance, slot) being sufficient to resolve a
// variable access to it's definition. as long as both resolver and interpreter (via environment) 1. define variables
// in the same sequential order and 2. scopes correspond to environments, the indices will match up.
record ResolvedVarInfo(int distance, int slot) {}

class Resolver implements Expr.Visitor<Void>, Stmt.Visitor<Void> {
  private final Interpreter interpreter;
  private final Stack<Map<String, Variable>> scopes = new Stack<Map<String, Variable>>();
  private FunctionType currentFunction = FunctionType.NONE;
  private ClassType currentClass = ClassType.NONE;

  private enum FunctionType {
    NONE,
    FUNCTION,
    INITIALIZER,
    METHOD
  }

  private enum ClassType {
    NONE,
    CLASS
  }

  private static class Variable {
    final Token name;
    State state;
    int slot;

    Variable(Token name, State state, int slot) {
      this.name = name;
      this.state = state;
      this.slot = slot;
    }

    private enum State {
      DECLARED,
      DEFINED,
      READ
    }
  }

  Resolver(Interpreter interpreter) {
    this.interpreter = interpreter;
  }

  void resolve(List<Stmt> statements) {
    for (Stmt statement : statements) {
      resolve(statement);
    }
  }

  @Override
  public Void visitBlockStmt(Stmt.Block stmt) {
    beginScope();
    resolve(stmt.statements);
    endScope();
    return null;
  }

  @Override
  public Void visitClassStmt(Stmt.Class stmt) {
    ClassType enclosingClass = currentClass;
    currentClass = ClassType.CLASS;

    declare(stmt.name);
    define(stmt.name);

    beginScope();
    // reminder: why we create a new scope here is a little tricky.
    // at runtime when we create handles for methods (LoxInstance.get()) we bind 'this' by wrapping
    // it in a closure. the scope we create here corresponds to that closure/env.
    // see book 12.6 for a great explanation.

    // scopes.peek().put("this", true);
    // note: book does above, perhaps to avoid defining a Token. for me it's easiest to just dummy the
    // token as below. should be fine.
    Token thiz = new Token(TokenType.THIS, "this", null, -1);
    declare(thiz);
    define(thiz);
    scopes.peek().get(thiz.lexeme).state = Variable.State.READ; // exempt from unused var check
    // todo: above: understand why I need to mark this as read.

    for (Stmt.Function method : stmt.methods) {
      FunctionType declaration = FunctionType.METHOD;
      if (method.name.lexeme.equals("init")) {
        declaration = FunctionType.INITIALIZER;
      }
      // reminder: method lookup is dynamic. they're accessed as properties on instances.
      // so no declare/define here.
      resolveFunction(method.function, declaration);
    }


    endScope();

    currentClass = enclosingClass;
    return null;
  }

  @Override
  public Void visitBreakStmt(Stmt.Break stmt) {
    return null;
  }

  @Override
  public Void visitExpressionStmt(Stmt.Expression stmt) {
    resolve(stmt.expression);
    return null;
  }

  @Override
  public Void visitFunctionStmt(Stmt.Function stmt) {
    declare(stmt.name);
    define(stmt.name);

    resolveFunction(stmt.function, FunctionType.FUNCTION);
    return null;
  }

  @Override
  public Void visitIfStmt(Stmt.If stmt) {
    resolve(stmt.condition);
    resolve(stmt.thenBranch);
    if (stmt.elseBranch != null) resolve(stmt.elseBranch);
    return null;
  }

  @Override
  public Void visitPrintStmt(Stmt.Print stmt) {
    resolve(stmt.expression);
    return null;
  }

  @Override
  public Void visitReturnStmt(Stmt.Return stmt) {
    if (currentFunction == FunctionType.NONE) {
      Lox.error(stmt.keyword, "Can't return from top-level code.");
    }

    if (stmt.value != null) {
      if (currentFunction == FunctionType.INITIALIZER) {
        Lox.error(stmt.keyword, "Can't return a value from an initializer.");
      }

      resolve(stmt.value);
    }

    return null;
  }

  @Override
  public Void visitVarStmt(Stmt.Var stmt) {
    declare(stmt.name);
    if (stmt.initializer != null) {
      resolve(stmt.initializer);
    }
    define(stmt.name);
    return null;
  }

  @Override
  public Void visitWhileStmt(Stmt.While stmt) {
    resolve(stmt.condition);
    resolve(stmt.body);
    return null;
  }

  @Override
  public Void visitAssignExpr(Expr.Assign expr) {
    resolve(expr.value);
    resolveLocal(expr, expr.name, false);
    return null;
  }

  @Override
  public Void visitBinaryExpr(Expr.Binary expr) {
    resolve(expr.left);
    resolve(expr.right);
    return null;
  }

  @Override
  public Void visitCallExpr(Expr.Call expr) {
    resolve(expr.callee);

    for (Expr argument : expr.arguments) {
      resolve(argument);
    }

    return null;
  }

  @Override
  public Void visitConditionalExpr(Expr.Conditional expr) {
    resolve(expr.conditional);
    resolve(expr.thenBranch);
    resolve(expr.elseBranch);
    return null;
  }

  @Override
  public Void visitFunctionExpr(Expr.Function expr) {
    resolveFunction(expr, FunctionType.FUNCTION);
    return null;
  }

  @Override
  public Void visitGetExpr(Expr.Get expr) {
    resolve(expr.object);
    return null;
  }

  @Override
  public Void visitGroupingExpr(Expr.Grouping expr) {
    resolve(expr.expression);
    return null;
  }

  @Override
  public Void visitLiteralExpr(Expr.Literal expr) {
    return null;
  }

  @Override
  public Void visitLogicalExpr(Expr.Logical expr) {
    resolve(expr.left);
    resolve(expr.right);
    return null;
  }

  @Override
  public Void visitSetExpr(Expr.Set expr) {
    resolve(expr.value);
    resolve(expr.object);
    return null;
  }

  @Override
  public Void visitThisExpr(Expr.This expr) {
    if (currentClass == ClassType.NONE) {
      Lox.error(expr.keyword, "Can't use 'this' outside of a class.");
      return null;
    }

    resolveLocal(expr, expr.keyword, true);
    return null;
  }

  @Override
  public Void visitUnaryExpr(Expr.Unary expr) {
    resolve(expr.right);
    return null;
  }

  @Override
  public Void visitVariableExpr(Expr.Variable expr) {
    if (!scopes.isEmpty() &&
        scopes.peek().containsKey(expr.name.lexeme) &&
        scopes.peek().get(expr.name.lexeme).state == Variable.State.DECLARED) {
      Lox.error(expr.name,
          "Can't read local variable in its own initializer.");
    }

    resolveLocal(expr, expr.name, true);
    return null;
  }

  private void resolve(Stmt stmt) {
    stmt.accept(this);
  }

  void resolve(Expr expr) {
    expr.accept(this);
  }

  private void beginScope() {
    scopes.push(new HashMap<String, Variable>());
  }

  private void endScope() {
    Map<String, Variable> scope = scopes.pop();

    for (Map.Entry<String, Variable> entry : scope.entrySet()) {
      if (entry.getValue().state == Variable.State.DEFINED) {
        Lox.error(entry.getValue().name, "Local variable is not used.");
      }
    }
  }

  private void declare(Token name) {
    if (scopes.isEmpty()) return;

    Map<String, Variable> scope = scopes.peek();
    if (scope.containsKey(name.lexeme)) {
      Lox.error(name, "Already a variable with this name in this scope.");
    }
    scope.put(name.lexeme, new Variable(name, Variable.State.DECLARED, scope.size()));
  }

  private void define(Token name) {
    if (scopes.isEmpty()) return;
    scopes.peek().get(name.lexeme).state = Variable.State.DEFINED;
  }

  private void resolveLocal(Expr expr, Token name, Boolean isRead) {
    for (int i = scopes.size() - 1; i >= 0; i--) {
      Variable v = scopes.get(i).get(name.lexeme);
      if (v != null) {
        int distance = scopes.size() - 1 - i;
        interpreter.resolve(expr, new ResolvedVarInfo(distance, v.slot));

        // Mark it as read
        if (isRead) {
          scopes.get(i).get(name.lexeme).state = Variable.State.READ;
        }
        return;
      }
    }

    // Not found. Assume it is global.
  }

  private void resolveFunction(Expr.Function expr, FunctionType type) {
    FunctionType enclosingFunction = currentFunction;
    currentFunction = type;

    beginScope();
    for (Token param : expr.params) {
      declare(param);
      define(param);
    }
    resolve(expr.body);
    endScope();
    currentFunction = enclosingFunction;
  }
}
