package org.fpeterek.til.typechecking.sentence

import org.fpeterek.til.typechecking.sentence.isexecutable.NonExecutable
import org.fpeterek.til.typechecking.types.AtomicType
import org.fpeterek.til.typechecking.types.Type
import org.fpeterek.til.typechecking.types.ConstructionType
import org.fpeterek.til.typechecking.types.Unknown
import org.fpeterek.til.typechecking.util.SrcPosition

class Literal(
    val value: String,
    srcPos: SrcPosition,
    type: Type = Unknown
) : Construction(constructedType=type, constructionType=ConstructionType, srcPos), NonExecutable {

    init {
        when (type) {
            is AtomicType, Unknown -> Unit
            else -> {
                println(value)
                throw RuntimeException("Literal type must be either AtomicType or Unknown")
            }
        }
    }

    override fun toString() = value

}