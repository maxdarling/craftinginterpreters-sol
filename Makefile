# I copied util/java.make for now, since I don't know how make works :)
# todo: understand exactly how this works. to start: https://www.youtube.com/watch?v=_Ms1Z4xfqv4&t=6s

default: jlox

jlox:
	make -f util/java.make DIR=java PACKAGE=lox

# Compile and run the AST generator.
generate_ast:
	@ $(MAKE) -f util/java.make DIR=java PACKAGE=tool
	@ java -cp build/java com.craftinginterpreters.tool.GenerateAst \
			java/com/craftinginterpreters/lox

.PHONY: jlox generate_ast