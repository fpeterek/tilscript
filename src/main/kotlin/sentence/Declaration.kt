package org.fpeterek.til.typechecking.sentence

import org.antlr.v4.codegen.model.decl.Decl
import org.fpeterek.til.typechecking.astprocessing.result.TypeName
import org.fpeterek.til.typechecking.contextrecognition.Context
import org.fpeterek.til.typechecking.reporting.Report
import org.fpeterek.til.typechecking.types.FunctionType
import org.fpeterek.til.typechecking.types.Type
import org.fpeterek.til.typechecking.types.TypeAlias
import org.fpeterek.til.typechecking.util.SrcPosition


sealed class Declaration(srcPos: SrcPosition, reports: List<Report>, context: Context) :
    Sentence(srcPos, reports, context) {

    override fun withContext(context: Context): Declaration = when (this) {
        is LiteralDeclaration  -> withContext(context)
        is TypeDefinition      -> withContext(context)
        is VariableDeclaration -> withContext(context)
        is FunctionDeclaration -> withContext(context)
    }

}

class LiteralDeclaration(
    val literals: List<Literal>, srcPos: SrcPosition, reports: List<Report> = listOf(),
    context: Context = Context.Unknown
) : Declaration(srcPos, reports, context) {

    val type
        get() = literals.first().constructedType
    val names
        get() = literals.asSequence().map { it.value }

    override fun withReport(report: Report) = withReports(listOf(report))

    override fun withReports(iterable: Iterable<Report>) =
        LiteralDeclaration(literals, position, reports + iterable, context)

    override fun withContext(context: Context) =
        LiteralDeclaration(literals.map { it.withContext(context) }, position, reports, context)

    override fun toString() = "${names.joinToString(separator=", ")}/$type"
    override fun tsString() = "${names.joinToString(separator=", ")}/${type.name}"
}

class TypeDefinition(
    val alias: TypeAlias, srcPos: SrcPosition, reports: List<Report> = listOf(),
    context: Context = Context.Unknown
) : Declaration(srcPos, reports, context) {

    override fun withReport(report: Report) = withReports(listOf(report))

    override fun withReports(iterable: Iterable<Report>) =
        TypeDefinition(alias, position, reports + iterable, context)

    override fun withContext(context: Context) =
        TypeDefinition(alias, position, reports, context)

    override fun toString() = "${alias.shortName} := ${alias.type}"

    override fun tsString() = "${alias.name} := ${alias.type.name}"
}

class VariableDeclaration(
    val variables: List<Variable>, srcPos: SrcPosition, reports: List<Report> = listOf(),
    context: Context = Context.Unknown
) : Declaration(srcPos, reports, context) {

    val type
        get() = variables.first().constructedType
    val names
        get() = variables.asSequence().map { it.name }

    override fun withReport(report: Report) = withReports(listOf(report))

    override fun withReports(iterable: Iterable<Report>) =
        VariableDeclaration(variables, position, reports + iterable, context)

    override fun withContext(context: Context) =
        VariableDeclaration(variables.map { it.withContext(context) }, position, reports, context)

    override fun toString() = "${names.joinToString(separator=", ")} -> $type"
    override fun tsString() = "${names.joinToString(separator=", ")} -> ${type.name}"
}

class FunctionDeclaration(
    val functions: List<TilFunction>, srcPos: SrcPosition, reports: List<Report> = listOf(),
    context: Context = Context.Unknown
) : Declaration(srcPos, reports, context) {

    val type
        get() = functions.first().constructedType

    val names
        get() = functions.asSequence().map { it.name }

    override fun withReport(report: Report) = withReports(listOf(report))

    override fun withReports(iterable: Iterable<Report>) =
        FunctionDeclaration(functions, position, reports + iterable, context)

    override fun withContext(context: Context) =
        FunctionDeclaration(functions.map { it.withContext(context) }, position, reports, context)

    override fun toString() = "${names.joinToString(separator=", ")}/$type"
    override fun tsString() = "${names.joinToString(separator=", ")}/${type.name}"
}

class FunctionDefinition(
    val name: String,
    val args: List<Variable>,
    val constructsType: Type,
    val construction: Construction,
    srcPos: SrcPosition,
    reports: List<Report> = listOf(),
    context: Context = Context.Unknown
) : Declaration(srcPos, reports, context) {

    val signature = FunctionType(listOf(constructsType) + args.map { it.constructedType })

    val tilFunction = TilFunction(name, srcPos, signature, reports, context)

    override fun withReports(iterable: Iterable<Report>) = FunctionDefinition(
        name, args, constructsType, construction, position, reports + iterable, context
    )

    override fun withReport(report: Report) = withReports(listOf(report))

    override fun tsString() =
        "defn $name(${args.joinToString(", ") { it.tsString() }}) -> ${constructsType.name} := ${construction.tsString()}"
}
