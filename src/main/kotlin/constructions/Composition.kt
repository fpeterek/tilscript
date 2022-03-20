package org.fpeterek.til.typechecking.constructions

import org.fpeterek.til.typechecking.constructions.isexecutable.Executable
import org.fpeterek.til.typechecking.types.Type
import org.fpeterek.til.typechecking.types.Unknown

class Composition(
    val function: Construction,
    val args: List<Construction>,
    constructedType: Type = Unknown,
    constructionType: Type = Unknown
) : Construction(constructedType, constructionType), Executable {

    override fun toString() = "[$function ${args.joinToString(" ")}]"

}
