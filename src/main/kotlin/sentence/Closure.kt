package org.fpeterek.til.typechecking.sentence

import org.fpeterek.til.typechecking.contextrecognition.Context
import org.fpeterek.til.typechecking.sentence.isexecutable.Executable
import org.fpeterek.til.typechecking.greek.GreekAlphabet
import org.fpeterek.til.typechecking.reporting.Report
import org.fpeterek.til.typechecking.types.ConstructionType
import org.fpeterek.til.typechecking.types.FunctionType
import org.fpeterek.til.typechecking.types.Type
import org.fpeterek.til.typechecking.types.Unknown
import org.fpeterek.til.typechecking.util.SrcPosition

class Closure(
    val variables: List<Variable>,
    val construction: Construction,
    srcPos: SrcPosition,
    constructedType: Type = Unknown,
    reports: List<Report> = listOf(),
    context: Context = Context.Unknown,
) : Construction(constructedType, ConstructionType, srcPos, reports, context), Executable {

    val functionType = when {
        construction !is Composition                    -> Unknown
        construction.constructedType == Unknown         -> Unknown
        variables.any { it.constructedType == Unknown } -> Unknown

        else -> FunctionType(construction.constructedType, variables.map { it.constructedType })
    }
    override fun withReport(report: Report) = withReports(listOf(report))

    override fun withReports(iterable: Iterable<Report>) =
        Closure(variables, construction, position, constructedType, reports + iterable, context)

    override fun withContext(context: Context) =
        Closure(variables, construction, position, constructedType, reports, context)

    override fun toString() = "${variables.joinToString(", ", prefix=GreekAlphabet.lambda) } $construction"

}
