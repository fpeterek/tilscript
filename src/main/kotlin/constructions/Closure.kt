package org.fpeterek.til.typechecking.constructions

import org.fpeterek.til.typechecking.constructions.isexecutable.Executable
import org.fpeterek.til.typechecking.greek.GreekAlphabet
import org.fpeterek.til.typechecking.types.FunctionType
import org.fpeterek.til.typechecking.types.Type
import org.fpeterek.til.typechecking.types.Unknown

class Closure(
    val variables: List<Variable>,
    val construction: Construction,
    constructedType: Type = Unknown,
    constructionType: Type = Unknown
) : Construction(constructedType, constructionType), Executable {

    val functionType = when {
        construction !is Composition                       -> Unknown
        construction.constructedType == Unknown            -> Unknown
        variables.any { it.constructedType == Unknown }    -> Unknown

        else -> FunctionType(construction.constructedType, variables.map { it.constructedType })
    }

    override fun toString() = "${variables.joinToString(", ", prefix=GreekAlphabet.lambda) } $construction"

}
