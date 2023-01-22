package org.fpeterek.tilscript.common.sentence

import org.fpeterek.tilscript.common.SrcPosition
import org.fpeterek.tilscript.common.reporting.Report
import org.fpeterek.tilscript.common.sentence.isexecutable.Executable
import org.fpeterek.tilscript.common.types.ConstructionType
import org.fpeterek.tilscript.common.types.Type
import org.fpeterek.tilscript.common.types.Unknown

class AttributeReference(
    val attrs: List<String>,
    srcPos: SrcPosition,
    type: Type = Unknown,
    reports: List<Report> = listOf(),
) : Construction(type, ConstructionType, srcPos, reports), Executable {

    init {
        attrs.forEach {
            if (!(it.first().isLetter() && it.first().isLowerCase())) {
                throw RuntimeException("Variable names must start with a lower case character")
            }
        }
    }

    override fun withReport(report: Report) = withReports(listOf(report))

    override fun withReports(iterable: Iterable<Report>) =
        AttributeReference(attrs, position, constructedType, reports + iterable)

    override fun toString() = attrs.joinToString("->")
}