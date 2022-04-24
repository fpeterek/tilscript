package org.fpeterek.til.typechecking.sentence

import org.fpeterek.til.typechecking.sentence.isexecutable.Executable
import org.fpeterek.til.typechecking.greek.GreekAlphabet
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
) : Construction(constructedType, ConstructionType, srcPos), Executable {

    val functionType = when {
        construction !is Composition                    -> Unknown
        construction.constructedType == Unknown         -> Unknown
        variables.any { it.constructedType == Unknown } -> Unknown

        else -> FunctionType(construction.constructedType, variables.map { it.constructedType })
    }

    override fun toString() = "${variables.joinToString(", ", prefix=GreekAlphabet.lambda) } $construction"

}
