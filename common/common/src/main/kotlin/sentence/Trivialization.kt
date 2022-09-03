package org.fpeterek.tilscript.common.sentence

import org.fpeterek.tilscript.common.reporting.Report
import org.fpeterek.tilscript.common.sentence.isexecutable.Executable
import org.fpeterek.tilscript.common.types.Type
import org.fpeterek.tilscript.common.types.Unknown
import org.fpeterek.tilscript.common.SrcPosition

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