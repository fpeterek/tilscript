package org.fpeterek.til.typechecking.interpreter.builtins

import org.fpeterek.til.typechecking.interpreter.OperatorFunction
import org.fpeterek.til.typechecking.interpreter.interpreterinterface.InterpreterInterface
import org.fpeterek.til.typechecking.sentence.*
import org.fpeterek.til.typechecking.tilscript.Builtins
import org.fpeterek.til.typechecking.types.GenericType
import org.fpeterek.til.typechecking.util.SrcPosition

object EqualityOperator : OperatorFunction(
    "=",
    Builtins.Bool,
    listOf(
        Variable("fst", SrcPosition(-1, -1), GenericType(1)),
        Variable("snd", SrcPosition(-1, -1), GenericType(1)),
    )
) {

    override fun apply(interpreter: InterpreterInterface, args: List<Construction>) = args
        .map(interpreter::interpret)
        .let { intArgs ->
            when (intArgs[0] == intArgs[1]) {
                true -> Builtins.True
                else -> Builtins.False
            }
        }

}