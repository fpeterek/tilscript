package org.fpeterek.tilscript.interpreter.interpreter.interpreterinterface

import org.fpeterek.tilscript.interpreter.sentence.Construction
import org.fpeterek.tilscript.interpreter.sentence.Nil
import org.fpeterek.tilscript.interpreter.sentence.Variable
import org.fpeterek.tilscript.interpreter.types.Type

abstract class EagerFunction(name: String, returns: Type, args: List<Variable>): FunctionInterface(name, returns, args) {

    override fun invoke(interpreter: InterpreterInterface, args: List<Construction>, ctx: FnCallContext): Construction {
        val cons = args.map(interpreter::interpret)

        val nilArg = cons.firstOrNull { it is Nil }

        if (nilArg != null) {
            return nilArg
        }

        checkArgCount(cons)
        checkArgTypes(interpreter, cons.map { it.constructionType })
        cons.zip(this.args).map { (value, variable) ->
//            println("Creating variable $variable = $value")
//            if (interpreter.getVariable(variable.name) != null) {
//                val v = interpreter.getVariable(variable.name)!!
//                println("Variable ${v.name} already exists")
//                println(interpreter.getVariable(v.value.toString()))
//            }
            interpreter.createLocal(variable, value)
        }

        val retval = apply(interpreter, cons, ctx)

        checkSignature(interpreter, retval.constructionType, cons.map { it.constructionType })

        return retval
    }

}
