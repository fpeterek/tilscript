package org.fpeterek.til.typechecking.sentence

import org.fpeterek.til.typechecking.sentence.isexecutable.IsExecutable
import org.fpeterek.til.typechecking.types.Type
import org.fpeterek.til.typechecking.types.Unknown

sealed class Construction(
    open val constructedType: Type = Unknown,
    val constructionType: Type = Unknown
) : Sentence(), IsExecutable
