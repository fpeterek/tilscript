package org.fpeterek.til.typechecking.constructions

import org.fpeterek.til.typechecking.greek.GreekAlphabet
import org.fpeterek.til.typechecking.types.FunctionType
import org.fpeterek.til.typechecking.types.Type
import org.fpeterek.til.typechecking.types.Unknown

class Closure(
    val variables: List<Variable>,
    val construction: Construction,
    constructionType: Type = Unknown
) : Construction(constructionType) {

    val functionType = when {
        construction !is Composition           -> Unknown
        construction.constructsType == Unknown -> Unknown
        variables.any { it.type == Unknown }   -> Unknown

        else -> FunctionType(construction.constructsType, variables.map { it.type })
    }

    override fun toString() = "${variables.joinToString(", ", prefix=GreekAlphabet.lambda) } $construction"

}
