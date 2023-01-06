package org.fpeterek.tilscript.interpreter.interpreter

import org.fpeterek.tilscript.common.sentence.Construction
import org.fpeterek.tilscript.common.sentence.Declaration
import org.fpeterek.tilscript.common.sentence.Sentence

class ScriptContext(
    val filename: String,
    val sentences: List<Sentence>,
    val variableValuations: Map<String, Construction> = mapOf()
) {
    val declarations
        get() = sentences.filterIsInstance<Declaration>()
}
