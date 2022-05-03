package org.fpeterek.til.typechecking.sentence

import org.fpeterek.til.typechecking.contextrecognition.Context
import org.fpeterek.til.typechecking.exceptions.InvalidExecutionOrder
import org.fpeterek.til.typechecking.reporting.Report
import org.fpeterek.til.typechecking.sentence.isexecutable.Executable
import org.fpeterek.til.typechecking.types.ConstructionType
import org.fpeterek.til.typechecking.types.Type
import org.fpeterek.til.typechecking.types.Unknown
import org.fpeterek.til.typechecking.util.SrcPosition

class Execution(
    val construction: Construction,
    val executionOrder: Int,
    srcPos: SrcPosition,
    constructedType: Type = Unknown,
    reports: List<Report> = listOf(),
    context: Context = Context.Unknown,
) : Construction(constructedType, ConstructionType, srcPos, reports, context), Executable {

    init {
        if (executionOrder < 1 || executionOrder > 2) {
            throw InvalidExecutionOrder(executionOrder)
        }
    }

    override fun withReport(report: Report) = withReports(listOf(report))

    override fun withReports(iterable: Iterable<Report>) =
        Execution(construction, executionOrder, position, constructedType, reports + iterable, context)

    override fun withContext(context: Context) =
        Execution(construction, executionOrder, position, constructedType, reports, context)

    override fun toString() = "$executionOrder^$construction"
    override fun tsString() = "$executionOrder^${construction.tsString()}"

}
