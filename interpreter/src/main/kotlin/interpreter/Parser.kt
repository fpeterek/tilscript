package org.fpeterek.tilscript.interpreter.interpreter

import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import org.fpeterek.tilscript.common.SrcPosition
import org.fpeterek.tilscript.common.die
import org.fpeterek.tilscript.common.reporting.Report
import org.fpeterek.tilscript.common.reporting.ReportFormatter
import org.fpeterek.tilscript.interpreter.astprocessing.ASTConverter
import org.fpeterek.tilscript.interpreter.astprocessing.AntlrVisitor
import org.fpeterek.tilscript.interpreter.astprocessing.ErrorListener
import org.fpeterek.tilscript.interpreter.astprocessing.result.Sentences
import org.fpeterek.tilscript.parser.TILScriptLexer
import org.fpeterek.tilscript.parser.TILScriptParser


object Parser {

    private val reportFormatter = ReportFormatter()

    private fun printErrors(errors: Iterable<Report>, errorType: String) {
        println("-".repeat(80))
        println("$errorType errors")

        reportFormatter.terminalOutput(errors)

        println("-".repeat(80))
        println("\n")
    }

    fun parse(file: String): ScriptContext {
        val stream = CharStreams.fromFileName(file)

        val errorListener = ErrorListener(file)

        val lexer = TILScriptLexer(stream)
        lexer.removeErrorListeners()
        lexer.addErrorListener(errorListener)

        val parser = TILScriptParser(CommonTokenStream(lexer))
        parser.removeErrorListeners()
        parser.addErrorListener(errorListener)

        val start = parser.start()

        val sentences = try {
            AntlrVisitor(file).visit(start)
        } catch (ignored: Exception) {
            Sentences(listOf(), SrcPosition(0, 0, file))
        }

        if (errorListener.hasErrors) {
            printErrors(errorListener.errors, "Syntax")
            die("Syntax error occurred")
        }
        if (parser.numberOfSyntaxErrors > 0) {
            println("Parsing failed (likely due to a syntax error which couldn't be properly detected)")
            die("Syntax error occurred")
        }

        return ASTConverter.convert(file, sentences)
    }
}