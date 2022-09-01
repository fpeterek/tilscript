package org.fpeterek.tilscript.interpreter.interpreter.interpreterinterface

import org.fpeterek.tilscript.interpreter.sentence.Construction
import org.fpeterek.tilscript.interpreter.sentence.Variable
import org.fpeterek.tilscript.interpreter.types.Type

abstract class NilAcceptingFunction(name: String, returns: Type, args: List<Variable>): FunctionInterface(name, returns, args) {

    override val acceptsNil: Boolean
        get() = true

    override fun invoke(interpreter: InterpreterInterface, args: List<Construction>, ctx: FnCallContext) =
        runWithTypechecks(interpreter, args, ctx)

}