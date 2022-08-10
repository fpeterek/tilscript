package org.fpeterek.til.interpreter.tilscript

import org.fpeterek.til.interpreter.sentence.Sentence
import org.fpeterek.til.interpreter.types.TypeRepository

class ScriptContext(
    val sentences: List<Sentence>,
    val types: TypeRepository,
)
