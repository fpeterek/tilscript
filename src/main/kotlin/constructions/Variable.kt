package org.fpeterek.til.typechecking.constructions

import org.fpeterek.til.typechecking.constructions.isexecutable.Executable
import org.fpeterek.til.typechecking.types.Type
import org.fpeterek.til.typechecking.types.Unknown

class Variable(
    val name: String,
    type: Type = Unknown,
    constructionType: Type = Unknown) : Construction(type, constructionType), Executable {

    init {
        // TODO: Construction types may prove to be problematic
        if (constructionType is Unknown) {
            throw RuntimeException("Construction orders of variables must be known")
        }
    }

    override fun toString() = name

}
