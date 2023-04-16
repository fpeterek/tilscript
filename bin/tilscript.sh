#!/bin/sh

DIR_NAME=`dirname $0`
LIBS=`ls $DIR_NAME/libs/*.jar | tr '\n' ':'`
JAR_NAME="$DIR_NAME/tilscript.jar"

JARS="$JAR_NAME:$LIBS"
JARS=`echo $JARS | sed 's/:$//'`

java -cp $JARS org.fpeterek.tilscript.interpreter.MainKt "$@"

