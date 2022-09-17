package org.fpeterek.tilscript.interpreter.interpreter

import org.fpeterek.tilscript.common.sentence.TilFunction

data class CurrentRun(
    val topLevelFrame: StackFrame,
    val stack: MutableList<StackFrame>,
    val imports: MutableMap<String, RunContext>,
    val functions: MutableMap<String, TilFunction>,
    val symbolRepo: SymbolRepository,
    val typeRepo: TypeRepository,
)
