package org.fpeterek.til.typechecking.typechecker

import org.fpeterek.til.typechecking.sentence.*
import org.fpeterek.til.typechecking.types.ConstructionType
import org.fpeterek.til.typechecking.types.FunctionType
import org.fpeterek.til.typechecking.types.Type
import org.fpeterek.til.typechecking.types.Unknown

object TypeAssignment {
    fun Variable.assignType(type: Type) = Variable(name, position, type, reports)

    fun Trivialization.assignType(type: Type) = Trivialization(
        construction=construction,
        constructedType=type,
        constructionType=ConstructionType,
        srcPos=position,
        reports=reports,
    )

    fun Closure.assignType() = Closure(
        variables,
        construction,
        position,
        FunctionType(imageType=construction.constructedType, argTypes=variables.map { it.constructedType }),
        reports
    )

    fun Literal.assignType(type: Type) = Literal(value, position, type, reports)

    fun TilFunction.assignType(type: FunctionType) = TilFunction(name, position, type, reports)
}
