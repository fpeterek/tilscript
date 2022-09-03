package org.fpeterek.tilscript.common.sentence

import org.fpeterek.tilscript.common.reporting.Report
import org.fpeterek.tilscript.common.sentence.isexecutable.Executable
import org.fpeterek.tilscript.common.types.ConstructionType
import org.fpeterek.tilscript.common.types.Type
import org.fpeterek.tilscript.common.types.Unknown
import org.fpeterek.tilscript.common.SrcPosition

class Variable(
    val name: String,
    srcPos: SrcPosition,
    type: Type = Unknown,
    reports: List<Report> = listOf(),
    val value: Construction? = null,
) : Construction(type, ConstructionType, srcPos, reports), Executable {

    init {
        if (!(name.first().isLetter() && name.first().isLowerCase())) {
            throw RuntimeException("Variable names must start with a lower case character")
        }
    }

    override fun equals(other: Any?) =
        other != null && other is Variable && other.name == name

    fun withValue(value: Construction) = Variable(name, position, constructedType, reports, value)

    override fun withReport(report: Report) = withReports(listOf(report))

    override fun withReports(iterable: Iterable<Report>) =
        Variable(name, position, constructedType, reports + iterable)

    override fun toString() = name
    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + (value?.hashCode() ?: 0)
        return result
    }
}
