package org.fpeterek.tilscript.common.sentence

import org.fpeterek.tilscript.common.reporting.Report
import org.fpeterek.tilscript.common.sentence.isexecutable.Executable
import org.fpeterek.tilscript.common.types.ConstructionType
import org.fpeterek.tilscript.common.types.Type
import org.fpeterek.tilscript.common.types.Unknown
import org.fpeterek.tilscript.common.SrcPosition
import org.fpeterek.tilscript.common.die

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
            die("Execution order must be 1 or 2 (received: $executionOrder)")
        }
    }

    override fun withReport(report: Report) = withReports(listOf(report))

    override fun withReports(iterable: Iterable<Report>) =
        Execution(construction, executionOrder, position, constructedType, reports + iterable)

    override fun toString() = "^$executionOrder$construction"
    override fun hashCode(): Int {
        var result = construction.hashCode()
        result = 31 * result + executionOrder
        return result
    }

}
