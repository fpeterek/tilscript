package org.fpeterek.tilscript.interpreter

import org.fpeterek.tilscript.interpreter.interpreter.Interpreter

fun runScript(filename: String) {
    Interpreter().interpretFile(filename)
}

fun main(args: Array<String>) = args.forEach(::runScript)
