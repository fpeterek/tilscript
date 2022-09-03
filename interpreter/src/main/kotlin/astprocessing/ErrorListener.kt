package org.fpeterek.tilscript.interpreter.astprocessing

import org.antlr.v4.runtime.BaseErrorListener
import org.antlr.v4.runtime.RecognitionException
import org.antlr.v4.runtime.Recognizer
import org.fpeterek.tilscript.common.reporting.Report
import org.fpeterek.tilscript.common.SrcPosition
import java.util.*

class ErrorListener(private val filename: String) : BaseErrorListener() {

    private val mutableErrors = mutableListOf<Report>()

    val errors: List<Report> = mutableErrors

    val hasErrors get() = errors.isNotEmpty()

    override fun syntaxError(
        recognizer: Recognizer<*, *>?,
        offendingSymbol: Any?,
        line: Int,
        char: Int,
        msg: String?,
        e: RecognitionException?
    ) {
        val position = SrcPosition(line, char, filename)
        val message = msg ?: "Invalid symbol '$offendingSymbol'"
        mutableErrors.add(Report(message, position))
    }

}
