package org.fpeterek.til.typechecking.sentence

import org.fpeterek.til.typechecking.exceptions.InvalidType
import org.fpeterek.til.typechecking.reporting.Report
import org.fpeterek.til.typechecking.sentence.isexecutable.NonExecutable
import org.fpeterek.til.typechecking.tilscript.Builtins
import org.fpeterek.til.typechecking.types.AtomicType
import org.fpeterek.til.typechecking.types.Type
import org.fpeterek.til.typechecking.types.Unknown
import org.fpeterek.til.typechecking.util.SrcPosition

sealed class Value(
    srcPos: SrcPosition,
    type: Type = Unknown,
    reports: List<Report> = listOf(),
) : Construction(constructedType=type, constructionType=type, srcPos, reports), NonExecutable {

    init {
        when (type) {
            is AtomicType, Unknown -> Unit
            else -> {
                throw InvalidType("Literal type must be either AtomicType or Unknown")
            }
        }
    }

    override fun withReport(report: Report): Value = withReports(listOf(report))
    abstract override fun withReports(iterable: Iterable<Report>): Value

}

class Symbol(
    val value: String,
    srcPos: SrcPosition,
    type: Type = Unknown,
    reports: List<Report> = listOf(),
) : Value(srcPos, type, reports) {

    override fun withReports(iterable: Iterable<Report>) =
        Symbol(value, position, constructedType, reports + iterable)

    override fun toString() = value
}

class Integral(
    val value: Long,
    srcPos: SrcPosition,
    reports: List<Report> = listOf(),
) : Value(srcPos, Builtins.Int, reports) {

    override fun withReports(iterable: Iterable<Report>) =
        Integral(value, position, reports + iterable)

    override fun toString() = value.toString()
}

class Real(
    val value: Double,
    srcPos: SrcPosition,
    reports: List<Report> = listOf(),
) : Value(srcPos, Builtins.Real, reports) {

    override fun withReports(iterable: Iterable<Report>) =
        Real(value, position, reports + iterable)

    override fun toString() = value.toString()
}

class Bool(
    val value: Boolean,
    srcPos: SrcPosition,
    reports: List<Report> = listOf(),
) : Value(srcPos, Builtins.Bool, reports) {

    override fun withReports(iterable: Iterable<Report>) =
        Bool(value, position, reports + iterable)

    override fun toString() = if (value) { "True" } else { "False" }
}

class Nil(
    srcPos: SrcPosition,
    reports: List<Report> = listOf(),
) : Value(srcPos, Unknown, reports) {

    override fun withReports(iterable: Iterable<Report>) =
        Nil(position, reports + iterable)

    override fun toString() = "Nil"
}
