package org.fpeterek.til.typechecking.sentence

import org.fpeterek.til.typechecking.reporting.Report
import org.fpeterek.til.typechecking.sentence.isexecutable.Executable
import org.fpeterek.til.typechecking.types.Type
import org.fpeterek.til.typechecking.types.Unknown
import org.fpeterek.til.typechecking.util.SrcPosition

class Trivialization(
    val construction: Construction,
    srcPos: SrcPosition,
    constructedType: Type = Unknown,
    constructionType: Type = Unknown,
    reports: List<Report> = listOf(),
) : Construction(
    constructedType=constructedType,
    constructionType=constructionType,
    srcPos,
    reports,
), Executable {

    override fun toString() = "'$construction"

    override fun withReport(report: Report) = withReports(listOf(report))

    override fun withReports(iterable: Iterable<Report>) =
        Trivialization(construction, position, constructedType, constructionType, reports + iterable)
}