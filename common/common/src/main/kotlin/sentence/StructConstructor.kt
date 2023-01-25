package org.fpeterek.tilscript.common.sentence

import org.fpeterek.tilscript.common.SrcPosition
import org.fpeterek.tilscript.common.reporting.Report
import org.fpeterek.tilscript.common.sentence.isexecutable.Executable
import org.fpeterek.tilscript.common.types.ConstructionType
import org.fpeterek.tilscript.common.types.Type

class StructConstructor(
    val struct: Type,
    val args: List<Construction>,
    srcPos: SrcPosition,
    reports: List<Report> = listOf(),
) : Construction(struct, ConstructionType, srcPos, reports), Executable {

    override fun withReport(report: Report) = withReports(listOf(report))

    override fun withReports(iterable: Iterable<Report>) =
        StructConstructor(struct, args, position, reports + iterable)

    override fun toString() =
        "{${struct.name} ${args.joinToString(" ")}}"
}
