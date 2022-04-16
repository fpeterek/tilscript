package org.fpeterek.til.typechecking.tilscript

import org.fpeterek.til.typechecking.sentence.Sentence
import org.fpeterek.til.typechecking.types.TypeRepository

class ScriptContext(
    val sentences: List<Sentence>,
    val types: TypeRepository,
)
