package org.fpeterek.tilscript.interpreter.interpreter.builtins

import org.fpeterek.tilscript.interpreter.interpreter.interpreterinterface.DefaultFunction
import org.fpeterek.tilscript.interpreter.interpreter.interpreterinterface.FnCallContext
import org.fpeterek.tilscript.interpreter.interpreter.interpreterinterface.InterpreterInterface
import org.fpeterek.tilscript.interpreter.sentence.*
import org.fpeterek.tilscript.interpreter.util.SrcPosition

object LogicFunctions {

    private val unary = listOf(
        Variable("fst", SrcPosition(-1, -1), Types.Bool),
    )

    private val binary = listOf(
        Variable("fst", SrcPosition(-1, -1), Types.Bool),
        Variable("snd", SrcPosition(-1, -1), Types.Bool),
    )

    private fun symbolicNil(ctx: FnCallContext) =
        Nil(ctx.position, reason="Cannot perform logic operations on symbolic values")

    object Not : DefaultFunction(
        "Not",
        Types.Bool,
        unary
    ) {
        override fun apply(interpreter: InterpreterInterface, args: List<Construction>, ctx: FnCallContext) =
            when {
                args[0] is Bool -> when ((args[0] as Bool).value) {
                    true -> Values.False
                    else -> Values.True
                }
                else -> symbolicNil(ctx)
            }
    }

    object And : DefaultFunction(
        "And",
        Types.Bool,
        binary
    ) {
        override fun apply(interpreter: InterpreterInterface, args: List<Construction>, ctx: FnCallContext) =
            when {
                args.all { it is Bool && it.value } -> Values.True
                args.all { it is Symbol } -> symbolicNil(ctx)
                else -> Values.False
            }
    }

    object Or : DefaultFunction(
        "Or",
        Types.Bool,
        binary
    ) {
        override fun apply(interpreter: InterpreterInterface, args: List<Construction>, ctx: FnCallContext) =
            when {
                args.any { it is Bool && it.value } -> Values.True
                args.all { it is Symbol } -> symbolicNil(ctx)
                else -> Values.False
            }
    }

    object Implies : DefaultFunction(
        "Implies",
        Types.Bool,
        binary
    ) {
        override fun apply(interpreter: InterpreterInterface, args: List<Construction>, ctx: FnCallContext) =
            when {
                args[0] is Bool   && !(args[0] as Bool).value -> Values.True
                args[1] is Bool   &&  (args[1] as Bool).value -> Values.True
                args[0] is Symbol ||   args[1] is Symbol      -> symbolicNil(ctx)
                else                                          -> Values.False
            }
    }

}