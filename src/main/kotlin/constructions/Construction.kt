package org.fpeterek.til.typechecking.constructions

import org.fpeterek.til.typechecking.constructions.isexecutable.IsExecutable
import org.fpeterek.til.typechecking.types.ConstructionType
import org.fpeterek.til.typechecking.types.Type
import org.fpeterek.til.typechecking.types.Unknown

sealed class Construction(
    open val constructedType: Type = Unknown,
    constructionType: Type = Unknown) : IsExecutable {

    val constructionType = when (constructionType) {
        is ConstructionType, is Unknown -> constructionType
        else -> throw RuntimeException("Construction type must by of type ConstructionType or Unknown")
    }

}
