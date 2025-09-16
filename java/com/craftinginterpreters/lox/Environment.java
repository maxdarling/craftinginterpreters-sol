package com.craftinginterpreters.lox;

import java.util.ArrayList;
import java.util.List;

class Environment {
  final Environment enclosing;
  private final List<Object> values = new ArrayList<>();

  static Object uninitialized = new Object();

  Environment() {
    enclosing = null;
  }

  Environment(Environment enclosing) {
    this.enclosing = enclosing;
  }

  int declare() {
    values.add(uninitialized);
    return values.size() - 1;
  }

  void define(Object value) {
    values.add(value);
  }

  Environment ancestor(int distance) {
    Environment environment = this;
    for (int i = 0; i < distance; i++) {
      environment = environment.enclosing;
    }

    return environment;
  }

  Object getAt(int distance, int idx) {
    return ancestor(distance).values.get(idx);
  }

  void assignAt(int distance, int idx, Object value) {
    ancestor(distance).values.set(idx, value);
  }
}
