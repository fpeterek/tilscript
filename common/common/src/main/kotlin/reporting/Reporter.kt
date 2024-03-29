package org.fpeterek.tilscript.common.reporting

import org.fpeterek.tilscript.common.sentence.*
import org.fpeterek.tilscript.common.types.StructType


object Reporter {

    private val Sentence.hasReports get() = reports.isNotEmpty()

    private fun containsReports(fnDef: FunctionDeclaration): Boolean =
        fnDef.hasReports || fnDef.functions.any(Reporter::containsReports)

    private fun containsReports(litDef: LiteralDeclaration): Boolean =
        litDef.hasReports || litDef.literals.any(Reporter::containsReports)

    private fun containsReports(typeDef: TypeDefinition): Boolean = typeDef.hasReports

    private fun containsReports(varDef: VariableDeclaration): Boolean =
        varDef.hasReports || varDef.variables.any(Reporter::containsReports)

    private fun containsReports(closure: Closure): Boolean =
        closure.hasReports || closure.variables.any(Reporter::containsReports) || containsReports(closure.construction)

    private fun containsReports(comp: Composition): Boolean =
        comp.hasReports || containsReports(comp.function) || comp.args.any(Reporter::containsReports)

    private fun containsReports(exec: Execution): Boolean =
        exec.hasReports || containsReports(exec.construction)

    private fun containsReports(lit: Value): Boolean = lit.hasReports

    private fun containsReports(fn: TilFunction): Boolean = fn.hasReports

    private fun containsReports(triv: Trivialization): Boolean = triv.hasReports || containsReports(triv.construction)

    private fun containsReports(variable: Variable): Boolean = variable.hasReports

    private fun containsReports(def: FunctionDefinition): Boolean = def.hasReports || containsReports(def.construction)

    private fun containsReports(def: VariableDefinition): Boolean =
        def.hasReports || containsReports(def.variable) || containsReports(def.construction)

    private fun containsReports(importStatement: ImportStatement): Boolean =
        importStatement.hasReports

    private fun containsReports(attrRef: AttributeReference): Boolean =
        attrRef.hasReports

    private fun containsReports(structDef: StructDefinition): Boolean =
        structDef.hasReports

    private fun containsReports(structCons: StructConstructor): Boolean =
        structCons.hasReports || structCons.args.any { it.hasReports }

    private fun containsReports(def: Declaration): Boolean = when (def) {
        is FunctionDeclaration -> containsReports(def)
        is LiteralDeclaration  -> containsReports(def)
        is TypeDefinition      -> containsReports(def)
        is VariableDeclaration -> containsReports(def)
        is FunctionDefinition  -> containsReports(def)
        is VariableDefinition  -> containsReports(def)
        is StructDefinition    -> containsReports(def)
    }

    private fun containsReports(construction: Construction): Boolean = when (construction) {
        is Closure            -> containsReports(construction)
        is Composition        -> containsReports(construction)
        is Execution          -> containsReports(construction)
        is Value              -> containsReports(construction)
        is TilFunction        -> containsReports(construction)
        is Trivialization     -> containsReports(construction)
        is Variable           -> containsReports(construction)
        is AttributeReference -> containsReports(construction)
        is StructConstructor  -> containsReports(construction)
    }

    fun containsReports(sentence: Sentence): Boolean = when (sentence) {
        is Declaration     -> containsReports(sentence)
        is Construction    -> containsReports(sentence)
        is ImportStatement -> containsReports(sentence)
    }

    fun containsReports(sentences: Iterable<Sentence>): Boolean = sentences.any(Reporter::containsReports)

    private fun reportsAsList(def: FunctionDeclaration): List<Report> =
        def.reports + def.functions.flatMap(Reporter::reportsAsList)

    private fun reportsAsList(def: LiteralDeclaration): List<Report> =
        def.reports + def.literals.flatMap(Reporter::reportsAsList)

    private fun reportsAsList(def: TypeDefinition): List<Report> = def.reports

    private fun reportsAsList(def: VariableDeclaration): List<Report> =
        def.reports + def.variables.flatMap(Reporter::reportsAsList)

    private fun reportsAsList(closure: Closure): List<Report> =
        closure.reports + closure.variables.flatMap(Reporter::reportsAsList) + reportsAsList(closure.construction)

    private fun reportsAsList(comp: Composition): List<Report> =
        comp.reports + reportsAsList(comp.function) + comp.args.flatMap(Reporter::reportsAsList)

    private fun reportsAsList(exec: Execution): List<Report> =
        exec.reports + reportsAsList(exec.construction)

    private fun reportsAsList(lit: Value): List<Report> = lit.reports

    private fun reportsAsList(fn: TilFunction): List<Report> = fn.reports

    private fun reportsAsList(triv: Trivialization): List<Report> = triv.reports + reportsAsList(triv.construction)

    private fun reportsAsList(variable: Variable): List<Report> = variable.reports

    private fun reportsAsList(def: FunctionDefinition): List<Report> = def.reports + reportsAsList(def.construction)

    private fun reportsAsList(def: VariableDefinition): List<Report> =
        def.reports + reportsAsList(def.variable) + reportsAsList(def.construction)

    private fun reportsAsList(importStatement: ImportStatement): List<Report> =
        importStatement.reports

    private fun reportsAsList(attrRef: AttributeReference): List<Report> =
        attrRef.reports

    private fun reportsAsList(structDef: StructDefinition): List<Report> =
        structDef.reports

    private fun reportsAsList(structCons: StructConstructor): List<Report> =
        structCons.reports + structCons.args.flatMap { it.reports }

    private fun reportsAsList(def: Declaration): List<Report> = when (def) {
        is FunctionDeclaration -> reportsAsList(def)
        is LiteralDeclaration  -> reportsAsList(def)
        is TypeDefinition      -> reportsAsList(def)
        is VariableDeclaration -> reportsAsList(def)
        is FunctionDefinition  -> reportsAsList(def)
        is VariableDefinition  -> reportsAsList(def)
        is StructDefinition    -> reportsAsList(def)
    }

    private fun reportsAsList(construction: Construction): List<Report> = when (construction) {
        is Closure            -> reportsAsList(construction)
        is Composition        -> reportsAsList(construction)
        is Execution          -> reportsAsList(construction)
        is Value              -> reportsAsList(construction)
        is TilFunction        -> reportsAsList(construction)
        is Trivialization     -> reportsAsList(construction)
        is Variable           -> reportsAsList(construction)
        is AttributeReference -> reportsAsList(construction)
        is StructConstructor  -> reportsAsList(construction)
    }

    fun reportsAsList(sentence: Sentence): List<Report> = when (sentence) {
        is Declaration     -> reportsAsList(sentence)
        is Construction    -> reportsAsList(sentence)
        is ImportStatement -> reportsAsList(sentence)
    }

    fun reportsAsList(sentences: Iterable<Sentence>): List<Report> = sentences.flatMap(Reporter::reportsAsList)
}
