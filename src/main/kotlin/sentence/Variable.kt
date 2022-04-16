package org.fpeterek.til.typechecking.sentence

import org.fpeterek.til.typechecking.sentence.isexecutable.Executable
import org.fpeterek.til.typechecking.types.ConstructionType
import org.fpeterek.til.typechecking.types.Type
import org.fpeterek.til.typechecking.types.Unknown

class Variable(
    val name: String,
    type: Type = Unknown) : Construction(type, ConstructionType), Executable {

    override fun toString() = name

}
