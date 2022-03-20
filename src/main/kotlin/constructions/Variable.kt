package org.fpeterek.til.typechecking.constructions

import org.fpeterek.til.typechecking.constructions.isexecutable.Executable
import org.fpeterek.til.typechecking.types.Type
import org.fpeterek.til.typechecking.types.Unknown

class Variable(
    val name: String,
    type: Type = Unknown,
    constructionType: Type = Unknown) : Construction(type, constructionType), Executable {

    override fun toString() = name

}
