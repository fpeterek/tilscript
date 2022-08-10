package org.fpeterek.til.interpreter.sentence

import org.fpeterek.til.interpreter.reporting.Report
import org.fpeterek.til.interpreter.util.SrcPosition

sealed class Sentence(val position: SrcPosition, val reports: List<Report>) {
    // Unfortunately, the withReport method cannot have a default implementation -> we want to
    // override the return value for every subclass, thus, we cannot just implement withReport as
    // withReports(listOf(report)) here
    abstract fun withReport(report: Report): Sentence
    abstract fun withReports(iterable: Iterable<Report>): Sentence
}
