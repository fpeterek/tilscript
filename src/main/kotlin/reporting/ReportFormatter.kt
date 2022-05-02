package org.fpeterek.til.typechecking.reporting

import java.io.File
import kotlin.math.max
import kotlin.math.min

class ReportFormatter(file: String) {

    private val lines = when {
        file.isEmpty() -> listOf()
        else -> File(file).readLines()
    }

    private fun formatReport(report: Report): String {

        val line = lines[report.line-1]
        val rightAvailable = line.length - report.char - 1
        val rightUnused = max(19 - rightAvailable, 0)
        val leftAvailable = report.char - 1

        val leftMax = 40 + rightUnused
        val takeLeft = min(leftAvailable, leftMax)

        val dropLeft = max(leftAvailable - takeLeft, 0)

        val positionIndicator = "(${report.line}, ${report.char+1}): "
        val trimmed = line.drop(dropLeft).take(60)

        /* Position indicator is bound to consist of at least 8 chars - two parens, */
        /* two spaces, comma, colon and two numbers. Thus, we can be sure there     */
        /* will be enough space for three ~ characters and a space character        */

        val pointer = " ".repeat(positionIndicator.length + report.char - dropLeft - 4) + "~~~" + " ^" + when {
            rightAvailable > 1 -> " " + "~".repeat(min(rightAvailable, 3))
            else -> ""
        }

        val padding = " ".repeat(positionIndicator.length)

        return positionIndicator + trimmed + "\n" +
                pointer + "\n" +
                padding + report.message
    }

    fun terminalOutput(reports: Iterable<Report>) = reports
        .asSequence()
        .map(::formatReport)
        .forEach {
            println()
            println(it)
        }

}
