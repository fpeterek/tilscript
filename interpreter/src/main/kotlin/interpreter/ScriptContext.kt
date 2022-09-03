package org.fpeterek.tilscript.interpreter.interpreter

import org.fpeterek.tilscript.common.sentence.Sentence

class ScriptContext(
    val sentences: List<Sentence>,
    val types: TypeRepository,
)
