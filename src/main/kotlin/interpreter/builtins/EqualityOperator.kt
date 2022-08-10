package org.fpeterek.til.interpreter.interpreter.builtins

import org.fpeterek.til.interpreter.interpreter.OperatorFunction
import org.fpeterek.til.interpreter.interpreter.interpreterinterface.InterpreterInterface
import org.fpeterek.til.interpreter.sentence.*
import org.fpeterek.til.interpreter.types.GenericType
import org.fpeterek.til.interpreter.util.SrcPosition

object EqualityOperator : OperatorFunction(
    "=",
    Types.Bool,
    listOf(
        Variable("fst", SrcPosition(-1, -1), GenericType(1)),
        Variable("snd", SrcPosition(-1, -1), GenericType(1)),
    )
) {

    override fun apply(interpreter: InterpreterInterface, args: List<Construction>) = when {
        args[0] is Nil || args[1] is Nil -> Values.False
        args[0] == args[1] -> Values.True
        else -> Values.False
    }

}