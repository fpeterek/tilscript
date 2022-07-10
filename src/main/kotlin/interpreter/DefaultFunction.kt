package org.fpeterek.til.typechecking.interpreter

import org.fpeterek.til.typechecking.interpreter.interpreterinterface.EagerFunction
import org.fpeterek.til.typechecking.interpreter.interpreterinterface.InterpreterInterface
import org.fpeterek.til.typechecking.sentence.Construction
import org.fpeterek.til.typechecking.sentence.Variable
import org.fpeterek.til.typechecking.types.Type

class DefaultFunction(
    name: String,
    returns: Type,
    args: List<Variable>,
    val body: Construction,
) : EagerFunction(name, returns, args) {

    // Args have been handled by parent class already,
    override fun apply(interpreter: InterpreterInterface, args: List<Construction>) =
        interpreter.interpret(body)

}