package org.fpeterek.tilscript.interpreter.interpreter.interpreterinterface

import org.fpeterek.tilscript.interpreter.sentence.Construction
import org.fpeterek.tilscript.interpreter.sentence.Variable
import org.fpeterek.tilscript.interpreter.types.Type

abstract class LazyFunction(name: String, returns: Type, args: List<Variable>): FunctionInterface(name, returns, args) {

    override fun invoke(interpreter: InterpreterInterface, args: List<Construction>): Construction {
        checkArgCount(args)
        return apply(interpreter, args)
    }

}