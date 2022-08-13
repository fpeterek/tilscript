package org.fpeterek.tilscript.interpreter.sentence

import org.fpeterek.tilscript.interpreter.reporting.Report
import org.fpeterek.tilscript.interpreter.sentence.isexecutable.IsExecutable
import org.fpeterek.tilscript.interpreter.types.Type
import org.fpeterek.tilscript.interpreter.types.Unknown
import org.fpeterek.tilscript.interpreter.util.SrcPosition

sealed class Construction(
    open val constructedType: Type = Unknown,
    val constructionType: Type = Unknown,
    srcPos: SrcPosition,
    reports: List<Report>,
) : Sentence(srcPos, reports), IsExecutable {

    override fun withReport(report: Report) = withReports(listOf(report))

    // We want to override the function here so that whenever withReports is called on a Construction
    // The method will return a Construction, not a Sentence
    override fun withReports(iterable: Iterable<Report>): Construction = when (this) {
        is Closure        -> withReports(iterable)
        is Composition    -> withReports(iterable)
        is Execution      -> withReports(iterable)
        is Value          -> withReports(iterable)
        is TilFunction    -> withReports(iterable)
        is Trivialization -> withReports(iterable)
        is Variable       -> withReports(iterable)
    }
}
