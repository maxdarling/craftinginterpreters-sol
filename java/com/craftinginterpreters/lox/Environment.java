package com.craftinginterpreters.lox;

import java.util.ArrayList;
import java.util.List;

class Environment {
  final Environment enclosing;
  private final List<Object> values = new ArrayList<>();

  Environment() {
    enclosing = null;
  }

  Environment(Environment enclosing) {
    this.enclosing = enclosing;
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
