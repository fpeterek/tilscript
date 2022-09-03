package org.fpeterek.tilscript.common.reporting

import org.fpeterek.tilscript.common.SrcPosition

data class Report(
    val message: String,
    val position: SrcPosition,
) {

    val line get() = position.line
    val char get() = position.char
    val file get() = position.file

    override fun toString() = "($file: $line, $char): $message"
}
