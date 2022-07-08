package org.fpeterek.til.typechecking

import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import org.fpeterek.til.parser.TILScriptLexer
import org.fpeterek.til.parser.TILScriptParser
import org.fpeterek.til.typechecking.astprocessing.ASTConverter
import org.fpeterek.til.typechecking.astprocessing.AntlrVisitor
import org.fpeterek.til.typechecking.astprocessing.ErrorListener
import org.fpeterek.til.typechecking.astprocessing.result.Sentences
import org.fpeterek.til.typechecking.namechecker.NameChecker
import org.fpeterek.til.typechecking.reporting.Report
import org.fpeterek.til.typechecking.reporting.ReportFormatter
import org.fpeterek.til.typechecking.reporting.Reporter
import org.fpeterek.til.typechecking.sentence.Sentence
import org.fpeterek.til.typechecking.typechecker.TypeChecker
import org.fpeterek.til.typechecking.types.SymbolRepository
import org.fpeterek.til.typechecking.types.TypeRepository
import org.fpeterek.til.typechecking.util.SrcPosition

fun printErrors(errors: Iterable<Report>, file: String, errorType: String) {
    println("-".repeat(80))
    println("File: $file")
    println("$errorType errors")

    ReportFormatter(file).terminalOutput(errors)

    println("-".repeat(80))
    println("\n")
}

private fun printErrorsForSentences(sentences: Iterable<Sentence>, file: String, errorType: String) =
    printErrors(Reporter.reportsAsList(sentences), file, errorType)

fun checkScript(filename: String) {
    val stream = CharStreams.fromFileName(filename)

    val errorListener = ErrorListener()

    val lexer = TILScriptLexer(stream)
    lexer.removeErrorListeners()
    lexer.addErrorListener(errorListener)
    val parser = TILScriptParser(CommonTokenStream(lexer))
    parser.removeErrorListeners()
    parser.addErrorListener(errorListener)

    val start = parser.start()

    val sentences = try {
        AntlrVisitor.visit(start)
    } catch (ignored: Exception) {
        Sentences(listOf(), SrcPosition(0, 0))
    }

    if (errorListener.hasErrors) {
        printErrors(errorListener.errors, filename, "Syntax")
        return
    }

    val script = ASTConverter.convert(sentences)

    val nameChecked = NameChecker.checkSymbols(script.sentences, SymbolRepository.withBuiltins())

    if (Reporter.containsReports(nameChecked)) {
        printErrorsForSentences(nameChecked, filename, "Name")
        return
    }

    val typeChecked = TypeChecker.process(
        nameChecked,
        symbolRepository = SymbolRepository.withBuiltins(),
        typeRepo = TypeRepository.withBuiltins()
    )

    if (Reporter.containsReports(typeChecked)) {
        printErrorsForSentences(typeChecked, filename, "Type")
    }

}

fun main(args: Array<String>) = args.forEach(::checkScript)
