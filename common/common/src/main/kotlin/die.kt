package org.fpeterek.tilscript.common

import org.fpeterek.tilscript.common.reporting.Report
import org.fpeterek.tilscript.common.reporting.ReportFormatter
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

fun die(report: Report): Nothing {
    println("** Error **")
    ReportFormatter().terminalOutput(report)
    exitProcess(-1)
}

fun die(msg: String, pos: SrcPosition): Nothing =
    die(Report(msg, pos))

