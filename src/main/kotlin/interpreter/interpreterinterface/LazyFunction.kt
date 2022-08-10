package org.fpeterek.til.interpreter.interpreter.interpreterinterface

import org.fpeterek.til.interpreter.sentence.Construction
import org.fpeterek.til.interpreter.sentence.Variable
import org.fpeterek.til.interpreter.types.Type

abstract class LazyFunction(name: String, returns: Type, args: List<Variable>): FunctionInterface(name, returns, args) {

    override fun invoke(interpreter: InterpreterInterface, args: List<Construction>): Construction {
        checkArgCount(args)
        return apply(interpreter, args)
    }

}