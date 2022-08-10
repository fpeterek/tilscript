package org.fpeterek.til.typechecking.interpreter.builtins

import org.fpeterek.til.typechecking.interpreter.OperatorFunction
import org.fpeterek.til.typechecking.interpreter.interpreterinterface.InterpreterInterface
import org.fpeterek.til.typechecking.sentence.*
import org.fpeterek.til.typechecking.types.GenericType
import org.fpeterek.til.typechecking.util.SrcPosition

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