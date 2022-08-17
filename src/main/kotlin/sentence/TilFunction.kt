package org.fpeterek.tilscript.interpreter.sentence

import org.fpeterek.tilscript.interpreter.interpreter.interpreterinterface.FunctionInterface
import org.fpeterek.tilscript.interpreter.reporting.Report
import org.fpeterek.tilscript.interpreter.sentence.isexecutable.NonExecutable
import org.fpeterek.tilscript.interpreter.types.*
import org.fpeterek.tilscript.interpreter.util.SrcPosition
import org.fpeterek.tilscript.interpreter.util.die


class TilFunction(
    val name: String,
    srcPosition: SrcPosition,
    type: Type = Unknown,
    reports: List<Report> = listOf(),
    val implementation: FunctionInterface? = null,
) : Construction(constructedType=type, constructionType=ConstructionType, srcPosition, reports),
    NonExecutable {

    override fun equals(other: Any?) =
        other != null && other is TilFunction && name == other.name

    override fun toString() = name

    val fullyTyped: Boolean
        get() = constructedType is FunctionType && constructedType.fullyTyped

    init {
        if (type !is Unknown && type !is FunctionType) {
            die("Type of TilFunction must be Unknown or FunctionType")
        }
    }

    override fun withReport(report: Report) = withReports(listOf(report))

    override fun withReports(iterable: Iterable<Report>) =
        TilFunction(name, position, constructedType, reports + iterable)

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + (implementation?.hashCode() ?: 0)
        return result
    }
}
