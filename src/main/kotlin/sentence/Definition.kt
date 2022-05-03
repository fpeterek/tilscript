package org.fpeterek.til.typechecking.sentence

import org.fpeterek.til.typechecking.contextrecognition.Context
import org.fpeterek.til.typechecking.reporting.Report
import org.fpeterek.til.typechecking.types.TypeAlias
import org.fpeterek.til.typechecking.util.SrcPosition


sealed class Definition(srcPos: SrcPosition, reports: List<Report>, context: Context) :
    Sentence(srcPos, reports, context) {

    override fun withContext(context: Context): Definition = when (this) {
        is LiteralDefinition  -> withContext(context)
        is TypeDefinition     -> withContext(context)
        is VariableDefinition -> withContext(context)
        is FunctionDefinition -> withContext(context)
    }

}

class LiteralDefinition(
    val literals: List<Literal>, srcPos: SrcPosition, reports: List<Report> = listOf(),
    context: Context = Context.Unknown
) : Definition(srcPos, reports, context) {

    val type
        get() = literals.first().constructedType
    val names
        get() = literals.asSequence().map { it.value }

    override fun withReport(report: Report) = withReports(listOf(report))

    override fun withReports(iterable: Iterable<Report>) =
        LiteralDefinition(literals, position, reports + iterable, context)

    override fun withContext(context: Context) =
        LiteralDefinition(literals.map { it.withContext(context) }, position, reports, context)

    override fun toString() = "${names.joinToString(separator=", ")}/$type"
    override fun tsString() = "${names.joinToString(separator=", ")}/${type.name}"
}

class TypeDefinition(
    val alias: TypeAlias, srcPos: SrcPosition, reports: List<Report> = listOf(),
    context: Context = Context.Unknown
) : Definition(srcPos, reports, context) {

    override fun withReport(report: Report) = withReports(listOf(report))

    override fun withReports(iterable: Iterable<Report>) =
        TypeDefinition(alias, position, reports + iterable, context)

    override fun withContext(context: Context) =
        TypeDefinition(alias, position, reports, context)

    override fun toString() = "${alias.shortName} := ${alias.type}"

    override fun tsString() = "${alias.name} := ${alias.type.name}"
}

class VariableDefinition(
    val variables: List<Variable>, srcPos: SrcPosition, reports: List<Report> = listOf(),
    context: Context = Context.Unknown
) : Definition(srcPos, reports, context) {

    val type
        get() = variables.first().constructedType
    val names
        get() = variables.asSequence().map { it.name }

    override fun withReport(report: Report) = withReports(listOf(report))

    override fun withReports(iterable: Iterable<Report>) =
        VariableDefinition(variables, position, reports + iterable, context)

    override fun withContext(context: Context) =
        VariableDefinition(variables.map { it.withContext(context) }, position, reports, context)

    override fun toString() = "${names.joinToString(separator=", ")} -> $type"
    override fun tsString() = "${names.joinToString(separator=", ")} -> ${type.name}"
}

class FunctionDefinition(
    val functions: List<TilFunction>, srcPos: SrcPosition, reports: List<Report> = listOf(),
    context: Context = Context.Unknown
) : Definition(srcPos, reports, context) {

    val type
        get() = functions.first().constructedType

    val names
        get() = functions.asSequence().map { it.name }

    override fun withReport(report: Report) = withReports(listOf(report))

    override fun withReports(iterable: Iterable<Report>) =
        FunctionDefinition(functions, position, reports + iterable, context)

    override fun withContext(context: Context) =
        FunctionDefinition(functions.map { it.withContext(context) }, position, reports, context)

    override fun toString() = "${names.joinToString(separator=", ")}/$type"
    override fun tsString() = "${names.joinToString(separator=", ")}/${type.name}"

}
