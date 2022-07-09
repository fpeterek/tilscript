package org.fpeterek.til.typechecking.interpreter.interpreterinterface

import org.fpeterek.til.typechecking.sentence.Construction
import org.fpeterek.til.typechecking.types.Type

abstract class LazyFunction(name: String, returns: Type, args: List<Type>): FunctionInterface(name, returns, args) {

    override fun invoke(interpreter: InterpreterInterface, args: List<Construction>): Construction {
        checkArgCount(args)
        return apply(interpreter, args)
    }

}