package org.fpeterek.til.typechecking.sentence

import org.fpeterek.til.typechecking.sentence.isexecutable.Executable
import org.fpeterek.til.typechecking.types.Type
import org.fpeterek.til.typechecking.types.Unknown
import org.fpeterek.til.typechecking.util.SrcPosition

class Trivialization(
    val construction: Construction,
    srcPos: SrcPosition,
    constructedType: Type = Unknown,
    constructionType: Type = Unknown,
) : Construction(
    constructedType=constructedType,
    constructionType=constructionType,
    srcPos
), Executable {

    override fun toString() = "'$construction"

}