package org.fpeterek.tilscript.interpreter.util

import kotlin.system.exitProcess

fun die(msg: String): Nothing {
    println("** Error **")
    msg.split("\n").forEach {
        print("  ** ")
        println(it)
    }
    exitProcess(-1)
}

fun die(ex: Exception): Nothing {
    println("** Error **")
    println(ex)
    ex.printStackTrace()
    println()
    println("Interpreter exited upon encountering exception ${ex.javaClass}")
    exitProcess(-2)
}
