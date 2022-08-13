package org.fpeterek.tilscript.interpreter.sentence

import org.fpeterek.tilscript.interpreter.reporting.Report
import org.fpeterek.tilscript.interpreter.sentence.isexecutable.Executable
import org.fpeterek.tilscript.interpreter.types.Type
import org.fpeterek.tilscript.interpreter.types.Unknown
import org.fpeterek.tilscript.interpreter.util.SrcPosition

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