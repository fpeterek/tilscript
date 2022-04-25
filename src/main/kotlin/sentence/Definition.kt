package org.fpeterek.til.typechecking.sentence

import org.fpeterek.til.typechecking.reporting.Report
import org.fpeterek.til.typechecking.types.TypeAlias
import org.fpeterek.til.typechecking.util.SrcPosition


sealed class Definition(srcPos: SrcPosition, reports: List<Report>) : Sentence(srcPos, reports)

class LiteralDefinition(val literals: List<Literal>, srcPos: SrcPosition, reports: List<Report> = listOf()) :
    Definition(srcPos, reports) {

    val type
        get() = literals.first().constructedType
    val names
        get() = literals.asSequence().map { it.value }

    override fun withReport(report: Report) = withReports(listOf(report))

    override fun withReports(iterable: Iterable<Report>) =
        LiteralDefinition(literals, position, reports + iterable)

    override fun toString() = "${names.joinToString(separator=", ")}/$type"
}

class TypeDefinition(val alias: TypeAlias, srcPos: SrcPosition, reports: List<Report> /*= listOf()*/) :
    Definition(srcPos, reports) {

    override fun withReport(report: Report) = withReports(listOf(report))

    override fun withReports(iterable: Iterable<Report>) =
        TypeDefinition(alias, position, reports + iterable)

    override fun toString() = "${alias.shortName} := ${alias.type}"
}

class VariableDefinition(val variables: List<Variable>, srcPos: SrcPosition, reports: List<Report> /*= listOf()*/) :
    Definition(srcPos, reports) {

    val type
        get() = variables.first().constructedType
    val names
        get() = variables.asSequence().map { it.name }

    override fun withReport(report: Report) = withReports(listOf(report))

    override fun withReports(iterable: Iterable<Report>) =
        VariableDefinition(variables, position, reports + iterable)

    override fun toString() = "${names.joinToString(separator=", ")} -> $type"
}

class FunctionDefinition(val functions: List<TilFunction>, srcPos: SrcPosition, reports: List<Report> /*= listOf()*/) :
    Definition(srcPos, reports) {

    val type
        get() = functions.first().constructedType

    val names
        get() = functions.asSequence().map { it.name }

    override fun withReport(report: Report) = withReports(listOf(report))

    override fun withReports(iterable: Iterable<Report>) =
        FunctionDefinition(functions, position, reports + iterable)

    override fun toString() = "${names.joinToString(separator=", ")}/$type"

}
