package org.fpeterek.til.interpreter.sentence

import org.fpeterek.til.interpreter.sentence.isexecutable.Executable
import org.fpeterek.til.interpreter.reporting.Report
import org.fpeterek.til.interpreter.types.ConstructionType
import org.fpeterek.til.interpreter.types.FunctionType
import org.fpeterek.til.interpreter.types.Type
import org.fpeterek.til.interpreter.types.Unknown
import org.fpeterek.til.interpreter.util.SrcPosition


class Closure(
    val variables: List<Variable>,
    val construction: Construction,
    srcPos: SrcPosition,
    constructedType: Type = Unknown,
    reports: List<Report> = listOf(),
) : Construction(constructedType, ConstructionType, srcPos, reports), Executable {

    override fun equals(other: Any?): Boolean {
        if (other == null || other !is Closure) {
            return false
        }

        if (variables.size != other.variables.size) {
            return false
        }

        return variables.zip(other.variables).all { (fst, snd) -> fst == snd }
                && construction == other.construction
    }

    val functionType = when {
        construction !is Composition                    -> Unknown
        construction.constructedType == Unknown         -> Unknown
        variables.any { it.constructedType == Unknown } -> Unknown

        else -> FunctionType(construction.constructedType, variables.map { it.constructedType })
    }

    override fun withReport(report: Report) = withReports(listOf(report))

    override fun withReports(iterable: Iterable<Report>) =
        Closure(variables, construction, position, constructedType, reports + iterable)

    override fun toString() =
        "${variables.joinToString(", ", prefix = "\\") { "${it.name}: ${it.constructedType.name}" }} $construction"

    override fun hashCode(): Int {
        var result = variables.hashCode()
        result = 31 * result + construction.hashCode()
        result = 31 * result + functionType.hashCode()
        return result
    }
}
