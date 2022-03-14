package org.fpeterek.til.typechecking.constructions

import org.fpeterek.til.typechecking.types.ConstructionType
import org.fpeterek.til.typechecking.types.Type
import org.fpeterek.til.typechecking.types.Unknown

sealed class Construction(constructionType: Type) {

    val constructionType = when (constructionType) {
        is ConstructionType, is Unknown -> constructionType
        else -> throw RuntimeException("Construction type must by of type ConstructionType or Unknown")
    }

}
