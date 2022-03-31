package org.fpeterek.til.typechecking.constructions

import org.fpeterek.til.typechecking.constructions.isexecutable.Executable
import org.fpeterek.til.typechecking.types.ConstructionType
import org.fpeterek.til.typechecking.types.Type
import org.fpeterek.til.typechecking.types.Unknown

class Variable(
    val name: String,
    type: Type = Unknown,
    constructionType: Type = ConstructionType(1)) : Construction(type, constructionType), Executable {

    /* Construction type is 1, unless specified otherwise                          */
    /* Construction type may be specified manually to i.e. allow double execution  */

    init {
        if (constructionType is Unknown) {
            throw RuntimeException("Construction orders of variables must be known")
        }
    }

    override fun toString() = name

}
