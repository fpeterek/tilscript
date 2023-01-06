package org.fpeterek.tilscript.interpreter.interpreter

import org.fpeterek.tilscript.common.sentence.TilFunction

class RunContext(
    val stack: MutableList<StackFrame>,
    val typeRepo: TypeRepository,
    val symbolRepo: SymbolRepository,
    val functions: MutableMap<String, TilFunction>,
    val imports: MutableSet<String>
) {
    val topLevelFrame
        get() = stack.first()

    val currentFrame
        get() = stack.last()
}