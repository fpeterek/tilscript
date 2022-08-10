package org.fpeterek.til.interpreter.sentence

import org.fpeterek.til.interpreter.exceptions.InvalidExecutionOrder
import org.fpeterek.til.interpreter.reporting.Report
import org.fpeterek.til.interpreter.sentence.isexecutable.Executable
import org.fpeterek.til.interpreter.types.ConstructionType
import org.fpeterek.til.interpreter.types.Type
import org.fpeterek.til.interpreter.types.Unknown
import org.fpeterek.til.interpreter.util.SrcPosition

class Execution(
    val construction: Construction,
    val executionOrder: Int,
    srcPos: SrcPosition,
    constructedType: Type = Unknown,
    reports: List<Report> = listOf(),
) : Construction(constructedType, ConstructionType, srcPos, reports), Executable {

    override fun equals(other: Any?): Boolean {
        if (other == null || other !is Execution) {
            return false
        }

        return executionOrder == other.executionOrder && construction == other.construction
    }

    init {
        if (executionOrder < 1 || executionOrder > 2) {
            throw InvalidExecutionOrder(executionOrder)
        }
    }

    override fun withReport(report: Report) = withReports(listOf(report))

    override fun withReports(iterable: Iterable<Report>) =
        Execution(construction, executionOrder, position, constructedType, reports + iterable)

    override fun toString() = "$executionOrder^$construction"
    override fun hashCode(): Int {
        var result = construction.hashCode()
        result = 31 * result + executionOrder
        return result
    }

}
