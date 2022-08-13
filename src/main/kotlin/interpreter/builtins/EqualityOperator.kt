package org.fpeterek.tilscript.interpreter.interpreter.builtins

import org.fpeterek.tilscript.interpreter.interpreter.OperatorFunction
import org.fpeterek.tilscript.interpreter.interpreter.interpreterinterface.InterpreterInterface
import org.fpeterek.tilscript.interpreter.sentence.*
import org.fpeterek.tilscript.interpreter.types.GenericType
import org.fpeterek.tilscript.interpreter.util.SrcPosition

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