package org.fpeterek.til.typechecking.reporting

import org.fpeterek.til.typechecking.sentence.*
import java.io.File
import kotlin.math.max
import kotlin.math.min

class Reporter private constructor(sourceFile: String = "") {

    companion object {
        fun containsReports(sentence: Sentence) = Reporter().containsReports(sentence)
        fun containsReports(sentences: Iterable<Sentence>) = Reporter().containsReports(sentences)

        fun reportsAsList(sentence: Sentence, sourceFile: String) =
            Reporter(sourceFile).reportsAsList(sentence)

        fun reportsAsList(sentences: Iterable<Sentence>, sourceFile: String) =
            Reporter(sourceFile).reportsAsList(sentences)
    }

    private val lines = when {
        sourceFile.isEmpty() -> listOf()
        else -> File(sourceFile).readLines()
    }

    private val Sentence.hasReports get() = reports.isNotEmpty()

    private val Sentence.formattedReports get() = reports.map(::formatReport)

    // TODO: Refactor, this function does not really belong here
    private fun formatReport(report: Report): String {

        val line = lines[report.line-1]
        val rightAvailable = line.length - report.char - 1
        val rightUnused = max(19 - rightAvailable, 0)
        val leftAvailable = report.char - 1

        val leftMax = 40 + rightUnused
        val takeLeft = min(leftAvailable, leftMax)

        val dropLeft = max(leftAvailable - takeLeft, 0)

        // Antler uses 1-based indexing for lines, but 0-based indexing for characters
        val positionIndicator = "(${report.line}, ${report.char+1}): "
        val trimmed = line.drop(dropLeft).take(60)

        /* Position indicator is bound to consist of at least 8 chars - two parens, */
        /* two spaces, comma, colon and two numbers. Thus, we can be sure there     */
        /* will be enough space for three ~ characters and a space character        */

        val pointer = " ".repeat(positionIndicator.length + report.char - dropLeft - 4) + "~~~" + " ^" + when {
            rightAvailable > 1 -> " " + "~".repeat(min(rightAvailable, 3))
            else -> ""
        }

        val padding = " ".repeat(positionIndicator.length)

        return positionIndicator + trimmed + "\n" +
                pointer + "\n" +
                padding + report.message
    }

    private fun containsReports(fnDef: FunctionDefinition): Boolean =
        fnDef.hasReports || fnDef.functions.any(::containsReports)

    private fun containsReports(litDef: LiteralDefinition): Boolean =
        litDef.hasReports || litDef.literals.any(::containsReports)

    private fun containsReports(typeDef: TypeDefinition): Boolean = typeDef.hasReports

    private fun containsReports(varDef: VariableDefinition): Boolean =
        varDef.hasReports || varDef.variables.any(::containsReports)

    private fun containsReports(closure: Closure): Boolean =
        closure.hasReports || closure.variables.any(::containsReports) || containsReports(closure.construction)

    private fun containsReports(comp: Composition): Boolean =
        comp.hasReports || containsReports(comp.function) || comp.args.any(::containsReports)

    private fun containsReports(exec: Execution): Boolean =
        exec.hasReports || containsReports(exec.construction)

    private fun containsReports(lit: Literal): Boolean = lit.hasReports

    private fun containsReports(fn: TilFunction): Boolean = fn.hasReports

    private fun containsReports(triv: Trivialization): Boolean = triv.hasReports || containsReports(triv.construction)

    private fun containsReports(variable: Variable): Boolean = variable.hasReports

    private fun containsReports(def: Definition): Boolean = when (def) {
        is FunctionDefinition -> containsReports(def)
        is LiteralDefinition  -> containsReports(def)
        is TypeDefinition     -> containsReports(def)
        is VariableDefinition -> containsReports(def)
    }

    private fun containsReports(construction: Construction): Boolean = when (construction) {
        is Closure        -> containsReports(construction)
        is Composition    -> containsReports(construction)
        is Execution      -> containsReports(construction)
        is Literal        -> containsReports(construction)
        is TilFunction    -> containsReports(construction)
        is Trivialization -> containsReports(construction)
        is Variable       -> containsReports(construction)
    }

    fun containsReports(sentence: Sentence): Boolean = when (sentence) {
        is Definition   -> containsReports(sentence)
        is Construction -> containsReports(sentence)
    }

    fun containsReports(sentences: Iterable<Sentence>): Boolean = sentences.any(::containsReports)

    private fun reportsAsList(def: FunctionDefinition): List<String> =
        def.formattedReports + def.functions.flatMap(::reportsAsList)

    private fun reportsAsList(def: LiteralDefinition): List<String> =
        def.formattedReports + def.literals.flatMap(::reportsAsList)

    private fun reportsAsList(def: TypeDefinition): List<String> = def.formattedReports

    private fun reportsAsList(def: VariableDefinition): List<String> =
        def.formattedReports + def.variables.flatMap(::reportsAsList)

    private fun reportsAsList(closure: Closure): List<String> =
        closure.formattedReports + closure.variables.flatMap(::reportsAsList) + reportsAsList(closure.construction)

    private fun reportsAsList(comp: Composition): List<String> =
        comp.formattedReports + reportsAsList(comp.function) + comp.args.flatMap(::reportsAsList)

    private fun reportsAsList(exec: Execution): List<String> =
        exec.formattedReports + reportsAsList(exec.construction)

    private fun reportsAsList(lit: Literal): List<String> = lit.formattedReports

    private fun reportsAsList(fn: TilFunction): List<String> = fn.formattedReports

    private fun reportsAsList(triv: Trivialization): List<String> = triv.formattedReports + reportsAsList(triv.construction)

    private fun reportsAsList(variable: Variable): List<String> = variable.formattedReports

    private fun reportsAsList(def: Definition): List<String> = when (def) {
        is FunctionDefinition -> reportsAsList(def)
        is LiteralDefinition  -> reportsAsList(def)
        is TypeDefinition     -> reportsAsList(def)
        is VariableDefinition -> reportsAsList(def)
    }

    private fun reportsAsList(construction: Construction): List<String> = when (construction) {
        is Closure        -> reportsAsList(construction)
        is Composition    -> reportsAsList(construction)
        is Execution      -> reportsAsList(construction)
        is Literal        -> reportsAsList(construction)
        is TilFunction    -> reportsAsList(construction)
        is Trivialization -> reportsAsList(construction)
        is Variable       -> reportsAsList(construction)
    }

    fun reportsAsList(sentence: Sentence): List<String> = when (sentence) {
        is Definition   -> reportsAsList(sentence)
        is Construction -> reportsAsList(sentence)
    }

    fun reportsAsList(sentences: Iterable<Sentence>): List<String> = sentences.flatMap(::reportsAsList)
}
