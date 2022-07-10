package org.fpeterek.til.typechecking.interpreter.interpreterinterface

import org.fpeterek.til.typechecking.sentence.Construction
import org.fpeterek.til.typechecking.sentence.TilFunction
import org.fpeterek.til.typechecking.sentence.Variable
import org.fpeterek.til.typechecking.types.FunctionType
import org.fpeterek.til.typechecking.types.Type
import org.fpeterek.til.typechecking.util.SrcPosition

sealed class FunctionInterface constructor(
    val name: String,
    val returns: Type,
    val args: List<Variable>,
) {

    val argTypes = args.map { it.constructedType }

    val signature = FunctionType(returns, argTypes)
    val tilFunction = TilFunction(name, SrcPosition(-1, -1), signature)

    protected fun checkArgCount(fnArgs: List<Construction>) {
        if (fnArgs.size != args.size) {
            throw RuntimeException("Incorrect number of arguments in application of function '$name' (expected: ${args.size}, received: ${fnArgs.size})")
        }
    }

    protected fun checkArgTypeMatch(interpreter: InterpreterInterface, expected: Type, received: Type) {
        if (!interpreter.typesMatch(expected, received)) {
            throw RuntimeException("Invalid argument type in application of function '$name' (expected: $expected, received: $received)")
        }
    }

    protected fun checkArgTypes(interpreter: InterpreterInterface, fnArgs: List<Construction>) = argTypes
        .zip(fnArgs.map { it.constructedType })
        .forEach { (expected, received) -> checkArgTypeMatch(interpreter, expected, received) }

    abstract fun apply(interpreter: InterpreterInterface, args: List<Construction>): Construction

    abstract operator fun invoke(interpreter: InterpreterInterface, args: List<Construction>): Construction
}