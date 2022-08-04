package org.fpeterek.til.typechecking.sentence

import org.fpeterek.til.typechecking.exceptions.InvalidType
import org.fpeterek.til.typechecking.reporting.Report
import org.fpeterek.til.typechecking.sentence.isexecutable.NonExecutable
import org.fpeterek.til.typechecking.tilscript.Builtins
import org.fpeterek.til.typechecking.types.AtomicType
import org.fpeterek.til.typechecking.types.ListType
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

    override fun equals(other: Any?) =
        other != null && other is Symbol && other.value == value

    override fun withReports(iterable: Iterable<Report>) =
        Symbol(value, position, constructedType, reports + iterable)

    override fun toString() = value

    override fun hashCode() = value.hashCode()
}

class TypeRef(
    val type: Type,
    srcPos: SrcPosition,
    reports: List<Report> = listOf(),
) : Value(srcPos, Builtins.Type, reports) {

    override fun equals(other: Any?) =
        other != null && other is TypeRef && other.type.name == type.name

    override fun hashCode() = type.name.hashCode()

    override fun toString() = type.name

    override fun withReports(iterable: Iterable<Report>) =
        TypeRef(type, position, reports + iterable)

}

class Integral(
    val value: Long,
    srcPos: SrcPosition,
    reports: List<Report> = listOf(),
) : Value(srcPos, Builtins.Int, reports) {

    override fun equals(other: Any?) =
        other != null && other is Integral && other.value == value

    override fun withReports(iterable: Iterable<Report>) =
        Integral(value, position, reports + iterable)

    override fun toString() = value.toString()

    override fun hashCode() = value.hashCode()
}

class Real(
    val value: Double,
    srcPos: SrcPosition,
    reports: List<Report> = listOf(),
) : Value(srcPos, Builtins.Real, reports) {

    override fun equals(other: Any?) =
        other != null && other is Real && other.value == value

    override fun withReports(iterable: Iterable<Report>) =
        Real(value, position, reports + iterable)

    override fun toString() = value.toString()

    override fun hashCode() = value.hashCode()
}

class Text(
    val value: String,
    srcPos: SrcPosition,
    reports: List<Report> = listOf(),
) : Value(srcPos, Builtins.Text, reports) {

    override fun equals(other: Any?) =
        other != null && other is Text && other.value == value

    override fun hashCode() = value.hashCode()

    override fun withReports(iterable: Iterable<Report>) =
        Text(value, position, reports + iterable)

    override fun toString() = "\"$value\""
}

class Bool(
    val value: Boolean,
    srcPos: SrcPosition,
    reports: List<Report> = listOf(),
) : Value(srcPos, Builtins.Bool, reports) {

    override fun equals(other: Any?) =
        other != null && other is Bool && other.value == value

    override fun withReports(iterable: Iterable<Report>) =
        Bool(value, position, reports + iterable)

    override fun toString() = if (value) { "True" } else { "False" }

    override fun hashCode() = value.hashCode()
}

class Nil(
    srcPos: SrcPosition,
    reports: List<Report> = listOf(),
) : Value(srcPos, Unknown, reports) {

    override fun equals(other: Any?) = false

    override fun withReports(iterable: Iterable<Report>) =
        Nil(position, reports + iterable)

    override fun toString() = "Nil"

    override fun hashCode() = javaClass.hashCode()
}

sealed class TilList(
    valueType: Type,
    srcPos: SrcPosition,
    reports: List<Report> = listOf(),
) : Value(srcPos, ListType(valueType), reports) {

    val listType get() = constructionType as ListType
    val valueType get() = listType.type

    abstract fun contentsStr(): String

}

class ListCell(
    val head: Construction,
    val tail: TilList,
    valueType: Type,
    srcPos: SrcPosition,
    reports: List<Report> = listOf(),
) : TilList(valueType, srcPos, reports) {

    override fun equals(other: Any?): Boolean =
        other != null && other is ListCell && other.valueType == valueType && other.head == head && tail == other.tail

    override fun withReports(iterable: Iterable<Report>) = ListCell(
        head, tail, valueType, position, reports + iterable
    )

    override fun hashCode(): Int {
        var result = head.hashCode()
        result = 31 * result + tail.hashCode()
        result = 31 * result + valueType.hashCode()
        return result
    }

    private fun tailStr(): String = when (tail) {
        is EmptyList -> ""
        else -> ", ${tail.contentsStr()}"
    }

    override fun contentsStr(): String = "$head${tailStr()}"

    override fun toString() = "{ ${contentsStr()} }"
}

class EmptyList(
    valueType: Type,
    srcPos: SrcPosition,
    reports: List<Report> = listOf(),
) : TilList(valueType, srcPos, reports) {

    override fun withReports(iterable: Iterable<Report>) = EmptyList(valueType, position, reports + iterable)

    override fun contentsStr() = ""

    override fun toString() = "{}"

    override fun hashCode() = valueType.hashCode()

    override fun equals(other: Any?) = other != null && other is EmptyList && other.valueType == valueType
}
