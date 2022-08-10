package org.fpeterek.til.interpreter.sentence

import org.fpeterek.til.interpreter.reporting.Report
import org.fpeterek.til.interpreter.sentence.isexecutable.Executable
import org.fpeterek.til.interpreter.types.Type
import org.fpeterek.til.interpreter.types.Unknown
import org.fpeterek.til.interpreter.util.SrcPosition

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

    override fun equals(other: Any?): Boolean {
        return other != null && other is Trivialization && construction == other.construction
    }

    override fun toString() = "'$construction"

    override fun withReport(report: Report) = withReports(listOf(report))

    override fun withReports(iterable: Iterable<Report>) =
        Trivialization(construction, position, constructedType, constructionType, reports + iterable)
}