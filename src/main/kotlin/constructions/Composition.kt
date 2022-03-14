package org.fpeterek.til.typechecking.constructions

import org.fpeterek.til.typechecking.types.Type
import org.fpeterek.til.typechecking.types.Unknown

class Composition(
    val function: Construction,
    val args: List<Construction>,
    val constructsType: Type = Unknown,
    constructionType: Type = Unknown
) : Construction(constructionType) {

    override fun toString() = "[$function ${args.joinToString(" ")}]"

}
