package org.fpeterek.tilscript.interpreter.util

import org.fpeterek.tilscript.interpreter.sentence.Sentence
import org.fpeterek.tilscript.interpreter.types.TypeRepository

class ScriptContext(
    val sentences: List<Sentence>,
    val types: TypeRepository,
)
