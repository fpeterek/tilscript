#!/bin/sh

DIR_NAME=`dirname $0`
LIBS=`ls $DIR_NAME/libs/*.jar | tr '\n' ':' | sed 's/,$//'`
JAR_NAME="$DIR_NAME/tilscript.jar"

if [ -z '$LIBS' ]; then
    JARS="$JAR_NAME:$LIBS"
else
    JARS="$JAR_NAME"
fi

java -cp $JARS org.fpeterek.tilscript.interpreter.MainKt "$@"

