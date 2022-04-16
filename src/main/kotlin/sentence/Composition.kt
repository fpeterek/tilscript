package org.fpeterek.til.typechecking.sentence

import org.fpeterek.til.typechecking.sentence.isexecutable.Executable
import org.fpeterek.til.typechecking.types.ConstructionType
import org.fpeterek.til.typechecking.types.Type
import org.fpeterek.til.typechecking.types.Unknown

class Composition(
    val function: Construction,
    val args: List<Construction>,
    constructedType: Type = Unknown,
) : Construction(constructedType, ConstructionType), Executable {

    override fun toString() = "[$function ${args.joinToString(" ")}]"

}
