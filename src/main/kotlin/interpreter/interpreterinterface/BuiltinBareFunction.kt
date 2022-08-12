package org.fpeterek.til.interpreter.interpreter.interpreterinterface

import org.fpeterek.til.interpreter.sentence.Construction
import org.fpeterek.til.interpreter.sentence.Variable
import org.fpeterek.til.interpreter.types.Type

// This abstract class marks functions which make absolutely no assumptions about its arguments
// and delegates all handling to the programmer
// The user can never be presented with this interface as they do not deserve to wield such power
abstract class BuiltinBareFunction(
    name: String, returns: Type, args: List<Variable>): FunctionInterface(name, returns, args) {

    override fun invoke(interpreter: InterpreterInterface, args: List<Construction>) = apply(interpreter, args)

}