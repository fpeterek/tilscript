package org.fpeterek.til.interpreter.reporting

import org.fpeterek.til.interpreter.util.SrcPosition

data class Report(
    val message: String,
    val position: SrcPosition,
) {

    val line get() = position.line
    val char get() = position.char

    override fun toString() = "($line, $char): $message"
}
