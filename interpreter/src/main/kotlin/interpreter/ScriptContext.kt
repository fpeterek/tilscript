package org.fpeterek.tilscript.interpreter.interpreter

import org.fpeterek.tilscript.common.sentence.Construction
import org.fpeterek.tilscript.common.sentence.Declaration
import org.fpeterek.tilscript.common.sentence.Sentence

data class ScriptContext(
    val filename: String,
    val sentences: List<Sentence>,
    private val variableValuations: MutableMap<String, Construction> = mutableMapOf()
) {
    val declarations
        get() = sentences.filterIsInstance<Declaration>()

    fun putVar(name: String, value: Construction) {
        variableValuations[name] = value
    }

    fun getVar(name: String) = variableValuations[name]

    fun hasVar(name: String) = name in variableValuations
}
