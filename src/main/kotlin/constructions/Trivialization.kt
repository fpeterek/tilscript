package org.fpeterek.til.typechecking.constructions

import org.fpeterek.til.typechecking.constructions.isexecutable.Executable
import org.fpeterek.til.typechecking.types.Type

class Trivialization private constructor(
    val construction: Construction,
    constructedType: Type,
    constructionType: Type,
) : Construction(
    constructedType=constructedType,
    constructionType=constructionType
), Executable {

    override fun toString() = "'$construction"

}