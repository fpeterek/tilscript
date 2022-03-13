package org.fpeterek.til.typechecking.constructions

import org.fpeterek.til.typechecking.types.Type
import org.fpeterek.til.typechecking.types.Unknown


class TilFunction(
    val name: String,
    val type: Type = Unknown,
    constructionType: Type = Unknown
) : Construction(constructionType) {
    override fun toString() = name
}
