package org.fpeterek.tilscript.common.sentence

import org.fpeterek.tilscript.common.SrcPosition
import org.fpeterek.tilscript.common.reporting.Report
import org.fpeterek.tilscript.common.sentence.isexecutable.Executable
import org.fpeterek.tilscript.common.types.ConstructionType
import org.fpeterek.tilscript.common.types.StructType

class StructConstructor(
    val struct: StructType,
    val args: List<Construction>,
    srcPos: SrcPosition,
    reports: List<Report>,
) : Construction(struct, ConstructionType, srcPos, reports), Executable {

    override fun withReport(report: Report) = withReports(listOf(report))

    override fun withReports(iterable: Iterable<Report>) =
        StructConstructor(struct, args, position, reports + iterable)

    override fun toString() =
        "{${struct.name} ${args.joinToString(" ")}}"
}
