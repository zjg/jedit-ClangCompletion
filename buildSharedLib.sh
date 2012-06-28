#! /bin/bash

# arguments:
# $1 = java home directory (should end with '/jre')

javah \
   -classpath build/classes \
   -d c-src \
   clangcompletion.LibClang \
   || exit $?

gcc \
   -shared \
   -fPIC \
   -o build/libClangCompletionPluginLibClang.so \
   c-src/*.c \
   -I$1/../include \
   -lclang \
   || exit $?

