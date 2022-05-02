package org.fpeterek.til.typechecking.sentence

import org.fpeterek.til.typechecking.contextrecognition.Context
import org.fpeterek.til.typechecking.exceptions.InvalidType
import org.fpeterek.til.typechecking.reporting.Report
import org.fpeterek.til.typechecking.sentence.isexecutable.NonExecutable
import org.fpeterek.til.typechecking.types.AtomicType
import org.fpeterek.til.typechecking.types.Type
import org.fpeterek.til.typechecking.types.ConstructionType
import org.fpeterek.til.typechecking.types.Unknown
import org.fpeterek.til.typechecking.util.SrcPosition

class Literal(
    val value: String,
    srcPos: SrcPosition,
    type: Type = Unknown,
    reports: List<Report> = listOf(),
    context: Context = Context.Unknown,
) : Construction(constructedType=type, constructionType=ConstructionType, srcPos, reports, context), NonExecutable {

    init {
        when (type) {
            is AtomicType, Unknown -> Unit
            else -> {
                println(value)
                throw InvalidType("Literal type must be either AtomicType or Unknown")
            }
        }
    }

    override fun withReport(report: Report) = withReports(listOf(report))

    override fun withReports(iterable: Iterable<Report>) =
        Literal(value, position, constructedType, reports + iterable, context)

    override fun withContext(context: Context) =
        Literal(value, position, constructedType, reports, context)

    override fun toString() = value

}