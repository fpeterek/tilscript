package org.fpeterek.til.typechecking.sentence

import org.fpeterek.til.typechecking.contextrecognition.Context
import org.fpeterek.til.typechecking.exceptions.InvalidType
import org.fpeterek.til.typechecking.reporting.Report
import org.fpeterek.til.typechecking.sentence.isexecutable.NonExecutable
import org.fpeterek.til.typechecking.tilscript.Builtins
import org.fpeterek.til.typechecking.types.AtomicType
import org.fpeterek.til.typechecking.types.Type
import org.fpeterek.til.typechecking.types.ConstructionType
import org.fpeterek.til.typechecking.types.Unknown
import org.fpeterek.til.typechecking.util.SrcPosition

sealed class Literal(
    srcPos: SrcPosition,
    type: Type = Unknown,
    reports: List<Report> = listOf(),
    context: Context = Context.Unknown,
) : Construction(constructedType=type, constructionType=ConstructionType, srcPos, reports, context), NonExecutable {

    init {
        when (type) {
            is AtomicType, Unknown -> Unit
            else -> {
                throw InvalidType("Literal type must be either AtomicType or Unknown")
            }
        }
    }

    override fun withReport(report: Report): Literal = withReports(listOf(report))
    abstract override fun withReports(iterable: Iterable<Report>): Literal

    abstract override fun withContext(context: Context): Literal
}

class Symbol(
    val value: String,
    srcPos: SrcPosition,
    type: Type = Unknown,
    reports: List<Report> = listOf(),
    context: Context = Context.Unknown,
) : Literal(srcPos, type, reports, context) {

    override fun withReports(iterable: Iterable<Report>) =
        Symbol(value, position, constructedType, reports + iterable, context)

    override fun withContext(context: Context) =
        Symbol(value, position, constructedType, reports, context)

    override fun toString() = value
    override fun tsString() = value
}

class Integral(
    val value: Long,
    srcPos: SrcPosition,
    reports: List<Report> = listOf(),
    context: Context = Context.Unknown,
) : Literal(srcPos, Builtins.Nu, reports, context) {

    override fun withReports(iterable: Iterable<Report>) =
        Integral(value, position, reports + iterable, context)

    override fun withContext(context: Context) =
        Integral(value, position, reports, context)

    override fun toString() = value.toString()
    override fun tsString() = value.toString()
}

class Real(
    val value: Double,
    srcPos: SrcPosition,
    reports: List<Report> = listOf(),
    context: Context = Context.Unknown,
) : Literal(srcPos, Builtins.Eta, reports, context) {

    override fun withReports(iterable: Iterable<Report>) =
        Real(value, position, reports + iterable, context)

    override fun withContext(context: Context) =
        Real(value, position, reports, context)

    override fun toString() = value.toString()
    override fun tsString() = value.toString()
}

class Bool(
    val value: Boolean,
    srcPos: SrcPosition,
    reports: List<Report> = listOf(),
    context: Context = Context.Unknown,
) : Literal(srcPos, Builtins.Omicron, reports, context) {

    override fun withReports(iterable: Iterable<Report>) =
        Bool(value, position, reports + iterable, context)

    override fun withContext(context: Context) =
        Bool(value, position, reports, context)

    override fun toString() = if (value) { "True" } else { "False" }
    override fun tsString() = toString()
}

class Nil(
    srcPos: SrcPosition,
    reports: List<Report> = listOf(),
    context: Context = Context.Unknown,
) : Literal(srcPos, Unknown, reports, context) {

    override fun withReports(iterable: Iterable<Report>) =
        Nil(position, reports + iterable, context)

    override fun withContext(context: Context) =
        Nil(position, reports, context)

    override fun toString() = "Nil"
    override fun tsString() = "Nil"

}
