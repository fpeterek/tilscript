package org.fpeterek.tilscript.interpreter.interpreter

import org.fpeterek.tilscript.interpreter.interpreter.interpreterinterface.LazyFunction
import org.fpeterek.tilscript.interpreter.sentence.Variable
import org.fpeterek.tilscript.interpreter.types.Type

// Operators are lazy function because they require special handling on the side of the interpreter
// Arguments have to be evaluated before the operator is applied, and thus, no evaluation or typechecking
// is necessary when applying the operator itself
// Eager functions auto-evaluate their arguments, but this would lead to undesired double evaluation in this case
abstract class OperatorFunction(
    op: String,
    returns: Type,
    args: List<Variable>,
): LazyFunction(op, returns, args)
