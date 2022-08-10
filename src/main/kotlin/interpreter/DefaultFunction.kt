package org.fpeterek.til.interpreter.interpreter

import org.fpeterek.til.interpreter.interpreter.interpreterinterface.EagerFunction
import org.fpeterek.til.interpreter.interpreter.interpreterinterface.InterpreterInterface
import org.fpeterek.til.interpreter.sentence.Construction
import org.fpeterek.til.interpreter.sentence.Variable
import org.fpeterek.til.interpreter.types.Type

open class DefaultFunction(
    name: String,
    returns: Type,
    args: List<Variable>,
    val body: Construction,
) : EagerFunction(name, returns, args) {

    // Args have been handled by parent class already
    override fun apply(interpreter: InterpreterInterface, args: List<Construction>) =
        interpreter.interpret(body)

}