package org.fpeterek.tilscript.interpreter.astprocessing

import org.antlr.v4.runtime.BaseErrorListener
import org.antlr.v4.runtime.RecognitionException
import org.antlr.v4.runtime.Recognizer
import org.fpeterek.tilscript.interpreter.reporting.Report
import org.fpeterek.tilscript.interpreter.util.SrcPosition

class ErrorListener : BaseErrorListener() {

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
        val position = SrcPosition(line, char)
        val message = msg ?: "Invalid symbol '$offendingSymbol'"
        mutableErrors.add(Report(message, position))
    }

}
