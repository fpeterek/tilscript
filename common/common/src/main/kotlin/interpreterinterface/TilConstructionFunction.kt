package org.fpeterek.tilscript.common.interpreterinterface

import org.fpeterek.tilscript.common.interpreterinterface.DefaultFunction
import org.fpeterek.tilscript.common.interpreterinterface.FnCallContext
import org.fpeterek.tilscript.common.interpreterinterface.InterpreterInterface
import org.fpeterek.tilscript.common.sentence.Construction
import org.fpeterek.tilscript.common.sentence.Variable
import org.fpeterek.tilscript.common.types.Type

open class TilConstructionFunction(
    name: String,
    returns: Type,
    args: List<Variable>,
    val body: Construction,
) : DefaultFunction(name, returns, args) {

    override fun apply(interpreter: InterpreterInterface, args: List<Construction>, ctx: FnCallContext) =
        interpreter.interpret(body)

}