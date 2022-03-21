package org.fpeterek.til.typechecking.constructions

import org.fpeterek.til.typechecking.constructions.isexecutable.Executable
import org.fpeterek.til.typechecking.types.Type
import org.fpeterek.til.typechecking.types.Unknown

class Trivialization(
    val construction: Construction,
    constructedType: Type = Unknown,
    constructionType: Type = Unknown,
) : Construction(
    constructedType=constructedType,
    constructionType=constructionType
), Executable {

    override fun toString() = "'$construction"

}