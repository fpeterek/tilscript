package org.fpeterek.til.typechecking.interpreter.interpreterinterface

import org.fpeterek.til.typechecking.sentence.Construction
import org.fpeterek.til.typechecking.sentence.Nil
import org.fpeterek.til.typechecking.types.Type

abstract class EagerFunction(name: String, returns: Type, args: List<Type>): FunctionInterface(name, returns, args) {

    override fun invoke(interpreter: InterpreterInterface, args: List<Construction>): Construction {
        val cons = args.map(interpreter::interpret)

        val nilArg = args.firstOrNull { it is Nil }

        if (nilArg != null) {
            return nilArg
        }

        checkArgCount(args)
        checkArgTypes(interpreter, args)

        return apply(interpreter, cons)
    }

}
