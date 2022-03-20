package org.fpeterek.til.typechecking.constructions

import org.fpeterek.til.typechecking.constructions.isexecutable.NonExecutable
import org.fpeterek.til.typechecking.types.ConstructionType
import org.fpeterek.til.typechecking.types.FunctionType
import org.fpeterek.til.typechecking.types.Type
import org.fpeterek.til.typechecking.types.Unknown


class TilFunction(
    val name: String,
    val type: Type = Unknown,
) : Construction(constructedType=Unknown, constructionType=ConstructionType(order=1)), NonExecutable {
    override fun toString() = name

    init {
        when (type) {
            is Unknown, is FunctionType -> Unit
            else -> throw RuntimeException("Type of TilFunction must be Unknown or FunctionType")
        }
    }

}
