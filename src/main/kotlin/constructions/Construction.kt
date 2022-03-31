package org.fpeterek.til.typechecking.constructions

import org.fpeterek.til.typechecking.constructions.isexecutable.IsExecutable
import org.fpeterek.til.typechecking.types.Type
import org.fpeterek.til.typechecking.types.Unknown

sealed class Construction(
    open val constructedType: Type = Unknown,
    val constructionType: Type = Unknown
) : IsExecutable
