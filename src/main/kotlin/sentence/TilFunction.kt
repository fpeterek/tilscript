package org.fpeterek.til.typechecking.sentence

import org.fpeterek.til.typechecking.exceptions.InvalidType
import org.fpeterek.til.typechecking.reporting.Report
import org.fpeterek.til.typechecking.sentence.isexecutable.NonExecutable
import org.fpeterek.til.typechecking.types.*
import org.fpeterek.til.typechecking.util.SrcPosition


class TilFunction(
    val name: String,
    srcPosition: SrcPosition,
    type: Type = Unknown,
    reports: List<Report> = listOf(),
) : Construction(constructedType=type, constructionType=ConstructionType, srcPosition, reports),
    NonExecutable {

    override fun toString() = name

    val fullyTyped: Boolean
        get() = constructedType is FunctionType && constructedType.fullyTyped

    init {
        if (type !is Unknown && type !is FunctionType) {
            throw InvalidType("Type of TilFunction must be Unknown or FunctionType")
        }
    }

    override fun withReport(report: Report) = withReports(listOf(report))

    override fun withReports(iterable: Iterable<Report>) =
        TilFunction(name, position, constructedType, reports + iterable)
}
