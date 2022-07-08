package org.fpeterek.til.typechecking.typechecker

import org.fpeterek.til.typechecking.sentence.*
import org.fpeterek.til.typechecking.types.ConstructionType
import org.fpeterek.til.typechecking.types.FunctionType
import org.fpeterek.til.typechecking.types.Type

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

    fun Value.assignType(type: Type) = when (this) {
        is Symbol -> Symbol(value, position, type, reports)
        else -> this
    }

    fun TilFunction.assignType(type: FunctionType) = TilFunction(name, position, type, reports)
}
