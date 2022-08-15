package org.fpeterek.tilscript.interpreter.sentence

import org.fpeterek.tilscript.interpreter.reporting.Report
import org.fpeterek.tilscript.interpreter.util.SrcPosition

class ImportStatement(
    val file: String,
    srcPos: SrcPosition,
    reports: List<Report> = listOf(),
) : Sentence(srcPos, reports) {

    override fun withReport(report: Report) = withReports(listOf(report))

    override fun withReports(iterable: Iterable<Report>) =
        ImportStatement(file, position, reports + iterable)

    override fun toString() = "import \"$file\""

}
