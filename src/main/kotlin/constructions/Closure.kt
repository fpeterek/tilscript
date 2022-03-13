package org.fpeterek.til.typechecking.constructions

import org.fpeterek.til.typechecking.greek.GreekAlphabet
import org.fpeterek.til.typechecking.types.Type
import org.fpeterek.til.typechecking.types.Unknown

class Closure(
    val variables: List<Variable>,
    val construction: Construction,
    constructionType: Type = Unknown
) : Construction(constructionType) {

    override fun toString() = "${variables.joinToString(", ", prefix=GreekAlphabet.lambda) } $construction"

}
