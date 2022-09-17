package org.fpeterek.tilscript.interpreter.interpreter

import org.fpeterek.tilscript.common.sentence.Sentence

data class ScriptContext(
    val sentences: List<Sentence>,
)
