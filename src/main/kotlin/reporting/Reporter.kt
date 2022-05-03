package org.fpeterek.til.typechecking.reporting

import org.fpeterek.til.typechecking.sentence.*


object Reporter {

    private val Sentence.hasReports get() = reports.isNotEmpty()

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
        else              -> throw RuntimeException("Invalid state")
    }

    fun containsReports(sentence: Sentence): Boolean = when (sentence) {
        is Definition   -> containsReports(sentence)
        is Construction -> containsReports(sentence)
    }

    fun containsReports(sentences: Iterable<Sentence>): Boolean = sentences.any(::containsReports)

    private fun reportsAsList(def: FunctionDefinition): List<Report> =
        def.reports + def.functions.flatMap(::reportsAsList)

    private fun reportsAsList(def: LiteralDefinition): List<Report> =
        def.reports + def.literals.flatMap(::reportsAsList)

    private fun reportsAsList(def: TypeDefinition): List<Report> = def.reports

    private fun reportsAsList(def: VariableDefinition): List<Report> =
        def.reports + def.variables.flatMap(::reportsAsList)

    private fun reportsAsList(closure: Closure): List<Report> =
        closure.reports + closure.variables.flatMap(::reportsAsList) + reportsAsList(closure.construction)

    private fun reportsAsList(comp: Composition): List<Report> =
        comp.reports + reportsAsList(comp.function) + comp.args.flatMap(::reportsAsList)

    private fun reportsAsList(exec: Execution): List<Report> =
        exec.reports + reportsAsList(exec.construction)

    private fun reportsAsList(lit: Literal): List<Report> = lit.reports

    private fun reportsAsList(fn: TilFunction): List<Report> = fn.reports

    private fun reportsAsList(triv: Trivialization): List<Report> = triv.reports + reportsAsList(triv.construction)

    private fun reportsAsList(variable: Variable): List<Report> = variable.reports

    private fun reportsAsList(def: Definition): List<Report> = when (def) {
        is FunctionDefinition -> reportsAsList(def)
        is LiteralDefinition  -> reportsAsList(def)
        is TypeDefinition     -> reportsAsList(def)
        is VariableDefinition -> reportsAsList(def)
    }

    private fun reportsAsList(construction: Construction): List<Report> = when (construction) {
        is Closure        -> reportsAsList(construction)
        is Composition    -> reportsAsList(construction)
        is Execution      -> reportsAsList(construction)
        is Literal        -> reportsAsList(construction)
        is TilFunction    -> reportsAsList(construction)
        is Trivialization -> reportsAsList(construction)
        is Variable       -> reportsAsList(construction)
        else              -> throw RuntimeException("Invalid state")
    }

    fun reportsAsList(sentence: Sentence): List<Report> = when (sentence) {
        is Definition   -> reportsAsList(sentence)
        is Construction -> reportsAsList(sentence)
    }

    fun reportsAsList(sentences: Iterable<Sentence>): List<Report> = sentences.flatMap(::reportsAsList)
}
