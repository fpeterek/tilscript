package org.fpeterek.til.typechecking.sentence

import org.fpeterek.til.typechecking.sentence.isexecutable.Executable
import org.fpeterek.til.typechecking.types.ConstructionType
import org.fpeterek.til.typechecking.types.Type
import org.fpeterek.til.typechecking.types.Unknown
import org.fpeterek.til.typechecking.util.SrcPosition

class Execution(
    val construction: Construction,
    val executionOrder: Int,
    srcPos: SrcPosition,
    constructedType: Type = Unknown,
) : Construction(constructedType, ConstructionType, srcPos), Executable {

    init {
        if (executionOrder < 1 || executionOrder > 2) {
            throw RuntimeException("Execution order must be either 1 or 2")
        }
    }

    override fun toString() = "$executionOrder^$construction"

}
