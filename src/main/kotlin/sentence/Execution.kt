package org.fpeterek.tilscript.interpreter.sentence

import org.fpeterek.tilscript.interpreter.reporting.Report
import org.fpeterek.tilscript.interpreter.sentence.isexecutable.Executable
import org.fpeterek.tilscript.interpreter.types.ConstructionType
import org.fpeterek.tilscript.interpreter.types.Type
import org.fpeterek.tilscript.interpreter.types.Unknown
import org.fpeterek.tilscript.interpreter.util.SrcPosition
import org.fpeterek.tilscript.interpreter.util.die

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

    override fun toString() = "$executionOrder^$construction"
    override fun hashCode(): Int {
        var result = construction.hashCode()
        result = 31 * result + executionOrder
        return result
    }

}
