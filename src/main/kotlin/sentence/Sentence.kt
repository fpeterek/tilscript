package org.fpeterek.til.typechecking.sentence

import org.fpeterek.til.typechecking.contextrecognition.Context
import org.fpeterek.til.typechecking.reporting.Report
import org.fpeterek.til.typechecking.util.SrcPosition

sealed class Sentence(val position: SrcPosition, val reports: List<Report>, val context: Context) {
    // Unfortunately, the withReport method cannot have a default implementation -> we want to
    // override the return value for every subclass, thus, we cannot just implement withReport as
    // withReports(listOf(report)) here
    abstract fun withReport(report: Report): Sentence
    abstract fun withReports(iterable: Iterable<Report>): Sentence

    abstract fun withContext(context: Context): Sentence

    abstract fun tsString(): String
}
