package org.fpeterek.tilscript.interpreter.interpreter

import org.fpeterek.tilscript.common.sentence.Declaration

data class RunContext(
    val filename: String,
    val scriptContext: ScriptContext,
    val imports: Map<String, RunContext>
) {

    val declarations = scriptContext.sentences.asSequence().filterIsInstance<Declaration>()

}
