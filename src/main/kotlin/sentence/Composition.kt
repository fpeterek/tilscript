package org.fpeterek.til.typechecking.sentence

import org.fpeterek.til.typechecking.reporting.Report
import org.fpeterek.til.typechecking.sentence.isexecutable.Executable
import org.fpeterek.til.typechecking.types.ConstructionType
import org.fpeterek.til.typechecking.types.Type
import org.fpeterek.til.typechecking.types.Unknown
import org.fpeterek.til.typechecking.util.SrcPosition

class Composition(
    val function: Construction,
    val args: List<Construction>,
    srcPos: SrcPosition,
    constructedType: Type = Unknown,
    reports: List<Report> = listOf(),
) : Construction(constructedType, ConstructionType, srcPos, reports), Executable {

    override fun withReport(report: Report) = withReports(listOf(report))

    override fun withReports(iterable: Iterable<Report>) =
        Composition(function, args, position, constructedType, reports + iterable)

    override fun toString() = "[$function ${args.joinToString(" ")}]"

    override fun tsString() = "[$function ${args.joinToString(" ") { it.tsString() }}]"
}
