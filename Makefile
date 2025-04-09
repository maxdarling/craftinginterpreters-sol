# I copied util/java.make for now, since I don't know how make works :)
.PHONY: run

run:
	make -f util/java.make DIR=java PACKAGE=lox
