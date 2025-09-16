default: jlox

jlox:
	make -f util/java.make DIR=java PACKAGE=lox

test: jlox
	./test/test_runner.sh

clean:
	rm -rf build/

# Compile and run the AST generator.
generate_ast:
	@ $(MAKE) -f util/java.make DIR=java PACKAGE=tool
	@ java -cp build/java com.craftinginterpreters.tool.GenerateAst \
			java/com/craftinginterpreters/lox

.PHONY: jlox test clean generate_ast