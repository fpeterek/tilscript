package org.fpeterek.tilscript.interpreter.interpreter.interpreterinterface

import org.fpeterek.tilscript.interpreter.sentence.Construction
import org.fpeterek.tilscript.interpreter.sentence.Nil
import org.fpeterek.tilscript.interpreter.sentence.Variable
import org.fpeterek.tilscript.interpreter.types.Type

abstract class DefaultFunction(name: String, returns: Type, args: List<Variable>): FunctionInterface(name, returns, args) {

    override val acceptsNil get() = false

    override fun invoke(interpreter: InterpreterInterface, args: List<Construction>, ctx: FnCallContext): Construction {

        val nilArg = args.firstOrNull { it is Nil }

        if (nilArg != null) {
            return nilArg
        }

        return runWithTypechecks(interpreter, args, ctx)
    }

}
