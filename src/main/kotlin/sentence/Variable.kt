package org.fpeterek.til.interpreter.sentence

import org.fpeterek.til.interpreter.reporting.Report
import org.fpeterek.til.interpreter.sentence.isexecutable.Executable
import org.fpeterek.til.interpreter.types.ConstructionType
import org.fpeterek.til.interpreter.types.Type
import org.fpeterek.til.interpreter.types.Unknown
import org.fpeterek.til.interpreter.util.SrcPosition

class Variable(
    val name: String,
    srcPos: SrcPosition,
    type: Type = Unknown,
    reports: List<Report> = listOf(),
    val value: Construction? = null,
) : Construction(type, ConstructionType, srcPos, reports), Executable {

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
