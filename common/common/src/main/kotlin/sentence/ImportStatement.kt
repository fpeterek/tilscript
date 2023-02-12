package org.fpeterek.tilscript.common.sentence

import org.fpeterek.tilscript.common.reporting.Report
import org.fpeterek.tilscript.common.SrcPosition

class ImportStatement(
    val file: String,
    srcPos: SrcPosition,
    reports: List<Report> = listOf(),
) : Sentence(srcPos, reports) {

    override fun withReport(report: Report) = withReports(listOf(report))

    override fun withReports(iterable: Iterable<Report>) =
        ImportStatement(file, position, reports + iterable)

    override fun toString() = "Import \"$file\""

}
