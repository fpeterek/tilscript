package org.fpeterek.til.interpreter.interpreter.interpreterinterface

import org.fpeterek.til.interpreter.sentence.Construction
import org.fpeterek.til.interpreter.sentence.Nil
import org.fpeterek.til.interpreter.sentence.Variable
import org.fpeterek.til.interpreter.types.Type

abstract class EagerFunction(name: String, returns: Type, args: List<Variable>): FunctionInterface(name, returns, args) {

    override fun invoke(interpreter: InterpreterInterface, args: List<Construction>): Construction {
        val cons = args.map(interpreter::interpret)

        val nilArg = cons.firstOrNull { it is Nil }

        if (nilArg != null) {
            return nilArg
        }

        checkArgCount(cons)
        checkArgTypes(interpreter, cons.map { it.constructionType })
        cons.zip(this.args).map { (value, variable) ->
            interpreter.createLocal(variable, value)
        }

        val retval = apply(interpreter, cons)

        checkSignature(interpreter, retval.constructionType, cons.map { it.constructionType })

        return retval
    }

}
