#! /bin/bash

CFLAGS="-std=c++11"

g++ $CFLAGS -o meta/app $LDFLAGS source.cpp $EX_SOURCES 1> meta/compiler.out 2> meta/compiler.err

COMPILER_ERR=$?

