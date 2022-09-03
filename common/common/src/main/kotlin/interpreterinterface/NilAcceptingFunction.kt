package org.fpeterek.tilscript.common.interpreterinterface

import org.fpeterek.tilscript.common.sentence.Construction
import org.fpeterek.tilscript.common.sentence.Variable
import org.fpeterek.tilscript.common.types.Type

abstract class NilAcceptingFunction(name: String, returns: Type, args: List<Variable>): FunctionInterface(name, returns, args) {

    override val acceptsNil: Boolean
        get() = true

    override fun invoke(interpreter: InterpreterInterface, args: List<Construction>, ctx: FnCallContext) =
        runWithTypechecks(interpreter, args, ctx)

}