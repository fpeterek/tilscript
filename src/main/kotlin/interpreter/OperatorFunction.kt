package org.fpeterek.til.typechecking.interpreter

import org.fpeterek.til.typechecking.interpreter.interpreterinterface.EagerFunction
import org.fpeterek.til.typechecking.sentence.Variable
import org.fpeterek.til.typechecking.types.Type

abstract class OperatorFunction(
    fullName: String,
    val operator: String,
    returns: Type,
    args: List<Variable>,
): EagerFunction(fullName, returns, args) {
    val fullName get() = name
}
