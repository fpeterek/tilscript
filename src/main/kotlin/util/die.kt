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
