BUILD_DIR := build

default: clox jlox

# Remove all build outputs and intermediate files.
clean:
	@echo cleaning $(BUILD_DIR)...
	rm -rf $(BUILD_DIR)


# Compile a debug build of clox.
debug:
	@ $(MAKE) -f util/c.make NAME=cloxd MODE=debug SOURCE_DIR=clox

# Compile the C interpreter.
clox:
	@ $(MAKE) -f util/c.make NAME=clox MODE=release SOURCE_DIR=clox

# Compile the C interpreter as ANSI standard C++.
cpplox:
	@ $(MAKE) -f util/c.make NAME=cpplox MODE=debug CPP=true SOURCE_DIR=clox

# Compile the Java interpreter .java files to .class files.
jlox: 
	@ $(MAKE) -f util/java.make DIR=jlox/java PACKAGE=lox NAME=release
	@ touch $(BUILD_DIR)/jlox
	@ echo "#!/bin/bash" > $(BUILD_DIR)/jlox
#   @ echo "script_dir=$(dirname "$0")" >> $(BUILD_DIR)/jlox
	@ echo "java -cp ./build/release/jlox/java com.craftinginterpreters.lox.Lox \$$@" >> $(BUILD_DIR)/jlox
	@ chmod 755 ./build/jlox

.PHONY: clean clox debug default jlox