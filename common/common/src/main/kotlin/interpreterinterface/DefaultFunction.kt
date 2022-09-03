package org.fpeterek.tilscript.common.interpreterinterface

import org.fpeterek.tilscript.common.sentence.Construction
import org.fpeterek.tilscript.common.sentence.Nil
import org.fpeterek.tilscript.common.sentence.Variable
import org.fpeterek.tilscript.common.types.Type

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
