package org.fpeterek.tilscript.interpreter.sentence

import org.fpeterek.tilscript.interpreter.sentence.isexecutable.Executable
import org.fpeterek.tilscript.interpreter.reporting.Report
import org.fpeterek.tilscript.interpreter.types.ConstructionType
import org.fpeterek.tilscript.interpreter.types.FunctionType
import org.fpeterek.tilscript.interpreter.types.Type
import org.fpeterek.tilscript.interpreter.types.Unknown
import org.fpeterek.tilscript.interpreter.util.SrcPosition


class Closure(
    val variables: List<Variable>,
    val construction: Construction,
    val returnType: Type,
    srcPos: SrcPosition,
    reports: List<Report> = listOf(),
) : Construction(createFnType(returnType, variables), ConstructionType, srcPos, reports), Executable {

    companion object {
        private fun createFnType(returns: Type, vars: List<Variable>) =
            FunctionType(returns, vars.map { it.constructedType })
    }

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

    val functionType get() = constructedType

    override fun withReport(report: Report) = withReports(listOf(report))

    override fun withReports(iterable: Iterable<Report>) =
        Closure(variables, construction, returnType, position, reports + iterable)

    override fun toString() =
        "[${variables.joinToString(", ", prefix = "\\") { "${it.name}: ${it.constructedType.name}" }} -> $returnType: $construction]"

    override fun hashCode(): Int {
        var result = variables.hashCode()
        result = 31 * result + construction.hashCode()
        result = 31 * result + functionType.hashCode()
        return result
    }
}
