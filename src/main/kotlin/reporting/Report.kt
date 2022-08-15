package org.fpeterek.tilscript.interpreter.reporting

import org.fpeterek.tilscript.interpreter.util.SrcPosition

data class Report(
    val message: String,
    val position: SrcPosition,
) {

    val line get() = position.line
    val char get() = position.char
    val file get() = position.file

    override fun toString() = "($file: $line, $char): $message"
}
