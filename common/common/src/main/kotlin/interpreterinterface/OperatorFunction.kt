package org.fpeterek.tilscript.common.interpreterinterface

import org.fpeterek.tilscript.common.interpreterinterface.BuiltinVariadicFunction
import org.fpeterek.tilscript.common.sentence.Variable
import org.fpeterek.tilscript.common.types.Type

abstract class OperatorFunction(
    op: String,
    returns: Type,
    args: List<Variable>,
): BuiltinVariadicFunction(op, returns, args, false)
