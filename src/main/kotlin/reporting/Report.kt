package org.fpeterek.til.typechecking.reporting

import org.fpeterek.til.typechecking.util.SrcPosition

data class Report(
    val message: String,
    val position: SrcPosition,
) {

    val line get() = position.line
    val char get() = position.char

    override fun toString() = "($line, $char): $message"
}
