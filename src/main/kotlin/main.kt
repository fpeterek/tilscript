package org.fpeterek.til.interpreter

import org.fpeterek.til.interpreter.interpreter.Interpreter

fun runScript(filename: String) {
    Interpreter().interpretFile(filename)
}

fun main(args: Array<String>) = args.forEach(::runScript)
