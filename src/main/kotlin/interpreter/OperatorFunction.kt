package org.fpeterek.tilscript.interpreter.interpreter

import org.fpeterek.tilscript.interpreter.interpreter.interpreterinterface.BuiltinVariadicFunction
import org.fpeterek.tilscript.interpreter.sentence.Variable
import org.fpeterek.tilscript.interpreter.types.Type

abstract class OperatorFunction(
    op: String,
    returns: Type,
    args: List<Variable>,
): BuiltinVariadicFunction(op, returns, args, false)
