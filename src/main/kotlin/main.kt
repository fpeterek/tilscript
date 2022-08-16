package org.fpeterek.tilscript.interpreter

import org.fpeterek.tilscript.interpreter.interpreter.Interpreter

fun runScript(filename: String) = try {
    Interpreter().interpretFile(filename)
} catch (ex: Exception) {
    println("${ex.javaClass}: ${ex.message}")
}

fun main(args: Array<String>) = args.forEach(::runScript)
