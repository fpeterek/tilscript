package org.fpeterek.til.typechecking.sentence

import org.fpeterek.til.typechecking.reporting.Report
import org.fpeterek.til.typechecking.sentence.isexecutable.IsExecutable
import org.fpeterek.til.typechecking.types.Type
import org.fpeterek.til.typechecking.types.Unknown
import org.fpeterek.til.typechecking.util.SrcPosition

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
        is Literal        -> withReports(iterable)
        is TilFunction    -> withReports(iterable)
        is Trivialization -> withReports(iterable)
        is Variable       -> withReports(iterable)
    }
}
