package org.fpeterek.tilscript.interpreter.interpreter

import org.fpeterek.tilscript.interpreter.interpreter.interpreterinterface.DefaultFunction
import org.fpeterek.tilscript.interpreter.interpreter.interpreterinterface.FnCallContext
import org.fpeterek.tilscript.interpreter.interpreter.interpreterinterface.InterpreterInterface
import org.fpeterek.tilscript.interpreter.sentence.Construction
import org.fpeterek.tilscript.interpreter.sentence.Variable
import org.fpeterek.tilscript.interpreter.types.Type

open class TilConstructionFunction(
    name: String,
    returns: Type,
    args: List<Variable>,
    val body: Construction,
) : DefaultFunction(name, returns, args) {

    override fun apply(interpreter: InterpreterInterface, args: List<Construction>, ctx: FnCallContext) =
        interpreter.interpret(body)

}