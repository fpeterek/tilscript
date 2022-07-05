package org.fpeterek.til.typechecking.sentence

import org.fpeterek.til.typechecking.reporting.Report
import org.fpeterek.til.typechecking.types.FunctionType
import org.fpeterek.til.typechecking.types.Type
import org.fpeterek.til.typechecking.types.TypeAlias
import org.fpeterek.til.typechecking.util.SrcPosition


sealed class Declaration(srcPos: SrcPosition, reports: List<Report>) : Sentence(srcPos, reports)

class LiteralDeclaration(
    val literals: List<Symbol>, srcPos: SrcPosition, reports: List<Report> = listOf(),
) : Declaration(srcPos, reports) {

    val type
        get() = literals.first().constructedType
    val names
        get() = literals.asSequence().map { it.value }

    override fun withReport(report: Report) = withReports(listOf(report))

    override fun withReports(iterable: Iterable<Report>) =
        LiteralDeclaration(literals, position, reports + iterable)

    override fun toString() = "${names.joinToString(separator=", ")}/$type"
}

class TypeDefinition(
    val alias: TypeAlias, srcPos: SrcPosition, reports: List<Report> = listOf(),
) : Declaration(srcPos, reports) {

    override fun withReport(report: Report) = withReports(listOf(report))

    override fun withReports(iterable: Iterable<Report>) =
        TypeDefinition(alias, position, reports + iterable)

    override fun toString() = "${alias.name} := ${alias.type}"
}

class VariableDeclaration(
    val variables: List<Variable>, srcPos: SrcPosition, reports: List<Report> = listOf(),
) : Declaration(srcPos, reports) {

    val type
        get() = variables.first().constructedType
    val names
        get() = variables.asSequence().map { it.name }

    override fun withReport(report: Report) = withReports(listOf(report))

    override fun withReports(iterable: Iterable<Report>) =
        VariableDeclaration(variables, position, reports + iterable)

    override fun toString() = "${names.joinToString(separator=", ")} -> $type"
}

class VariableDefinition(
    val name: String,
    val constructsType: Type,
    val construction: Construction,
    srcPos: SrcPosition,
    reports: List<Report> = listOf(),
) : Declaration(srcPos, reports) {

    val variable
        get() = Variable(name, position, constructsType)

    override fun withReport(report: Report) = withReports(listOf(report))

    override fun withReports(iterable: Iterable<Report>) = VariableDefinition(
        name, constructsType, construction, position, reports + iterable
    )

    override fun toString() = "let $name -> $constructsType = $construction"
}

class FunctionDeclaration(
    val functions: List<TilFunction>, srcPos: SrcPosition, reports: List<Report> = listOf(),
) : Declaration(srcPos, reports) {

    val type
        get() = functions.first().constructedType

    val names
        get() = functions.asSequence().map { it.name }

    override fun withReport(report: Report) = withReports(listOf(report))

    override fun withReports(iterable: Iterable<Report>) =
        FunctionDeclaration(functions, position, reports + iterable)

    override fun toString() = "${names.joinToString(separator=", ")}/$type"
}

class FunctionDefinition(
    val name: String,
    val args: List<Variable>,
    val constructsType: Type,
    val construction: Construction,
    srcPos: SrcPosition,
    reports: List<Report> = listOf(),
) : Declaration(srcPos, reports) {

    val signature = FunctionType(listOf(constructsType) + args.map { it.constructedType })

    val tilFunction = TilFunction(name, srcPos, signature, reports)

    override fun withReports(iterable: Iterable<Report>) = FunctionDefinition(
        name, args, constructsType, construction, position, reports + iterable
    )

    override fun withReport(report: Report) = withReports(listOf(report))

    override fun toString() =
        "defn $name(${args.joinToString(", ")}) -> ${constructsType.name} = $construction"
}
