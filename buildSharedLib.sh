#! /bin/bash

# arguments:
# $1 = java home directory (should end with '/jre')

javah \
   -classpath build/classes \
   -d c-src \
   clangcompletion.LibClang \
   || exit $?

clang \
   -g \
   -shared \
   -fPIC \
   -Wall \
   -Werror \
   -I$1/../include \
   -lclang \
   -o build/libClangCompletionPluginLibClang.so \
   c-src/*.c \
   || exit $?

