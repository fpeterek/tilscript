package org.fpeterek.til.interpreter.interpreter.builtins

import org.fpeterek.til.interpreter.interpreter.interpreterinterface.EagerFunction
import org.fpeterek.til.interpreter.interpreter.interpreterinterface.InterpreterInterface
import org.fpeterek.til.interpreter.sentence.Bool
import org.fpeterek.til.interpreter.sentence.Construction
import org.fpeterek.til.interpreter.sentence.Symbol
import org.fpeterek.til.interpreter.sentence.Variable
import org.fpeterek.til.interpreter.util.SrcPosition

object LogicFunctions {

    private val unary = listOf(
        Variable("fst", SrcPosition(-1, -1), Types.Bool),
    )

    private val binary = listOf(
        Variable("fst", SrcPosition(-1, -1), Types.Bool),
        Variable("snd", SrcPosition(-1, -1), Types.Bool),
    )

    object Not : EagerFunction(
        "Not",
        Types.Bool,
        unary
    ) {
        override fun apply(interpreter: InterpreterInterface, args: List<Construction>) =
            when {
                args[0] is Bool -> when ((args[0] as Bool).value) {
                    true -> Values.False
                    else -> Values.True
                }
                else -> Values.Nil
            }
    }

    object And : EagerFunction(
        "And",
        Types.Bool,
        binary
    ) {
        override fun apply(interpreter: InterpreterInterface, args: List<Construction>) =
            when {
                args.all { it is Bool && it.value } -> Values.True
                args.all { it is Symbol } -> Values.Nil
                else -> Values.False
            }
    }

    object Or : EagerFunction(
        "Or",
        Types.Bool,
        binary
    ) {
        override fun apply(interpreter: InterpreterInterface, args: List<Construction>) =
            when {
                args.any { it is Bool && it.value } -> Values.True
                args.all { it is Symbol } -> Values.Nil
                else -> Values.False
            }
    }

    object Implies : EagerFunction(
        "Implies",
        Types.Bool,
        binary
    ) {
        override fun apply(interpreter: InterpreterInterface, args: List<Construction>) =
            when {
                args[0] is Bool   && !(args[0] as Bool).value -> Values.True
                args[1] is Bool   &&  (args[1] as Bool).value -> Values.True
                args[0] is Symbol ||   args[1] is Symbol      -> Values.Nil
                else                                          -> Values.False
            }
    }

}