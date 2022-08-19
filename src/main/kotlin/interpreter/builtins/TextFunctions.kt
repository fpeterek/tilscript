package org.fpeterek.tilscript.interpreter.interpreter.builtins

import org.fpeterek.tilscript.interpreter.interpreter.interpreterinterface.EagerFunction
import org.fpeterek.tilscript.interpreter.interpreter.interpreterinterface.FnCallContext
import org.fpeterek.tilscript.interpreter.interpreter.interpreterinterface.InterpreterInterface
import org.fpeterek.tilscript.interpreter.sentence.*
import org.fpeterek.tilscript.interpreter.util.SrcPosition

object TextFunctions {
    private val textVar = Variable("str", SrcPosition(-1, -1), Types.Text)
    private val text2Var = Variable("str2", SrcPosition(-1, -1), Types.Text)
    private val idxVar = Variable("idx", SrcPosition(-1, -1), Types.Int)

    object Char : EagerFunction(
        "Char",
        Types.Text,
        listOf(
            textVar,
            idxVar
        )
    ) {
        override fun apply(interpreter: InterpreterInterface, args: List<Construction>, ctx: FnCallContext): Construction {
            val str = args.first()
            val idx = args[1]

            if (idx !is Integral || str !is Text) {
                return Nil(ctx.position, reason="Char arguments must be non-symbolic")
            }

            val idxVal = idx.value.toInt()

            if (idxVal !in str.value.indices) {
                return Nil(ctx.position, reason="Text index out of range")
            }

            return Text(str.value[idxVal].toString(), str.position)
        }

    }

    object CatS : EagerFunction(
        "CatS",
        Types.Text,
        listOf(
            textVar,
            text2Var,
        )
    ) {
        override fun apply(interpreter: InterpreterInterface, args: List<Construction>, ctx: FnCallContext): Construction {
            val str = args.first()
            val str2 = args[1]

            if (str2 !is Text || str !is Text) {
                return Nil(ctx.position, reason="Cannot concatenate symbolic values")
            }

            return Text(str.value + str2.value, str.position)
        }

    }

    object HeadS : EagerFunction(
        "HeadS",
        Types.Text,
        listOf(
            textVar,
        )
    ) {
        override fun apply(interpreter: InterpreterInterface, args: List<Construction>, ctx: FnCallContext): Construction {
            val str = args.first()

            if (str !is Text) {
                return Nil(ctx.position, reason="Cannot take the head of a symbolic value")
            }

            if (str.value.isEmpty()) {
                return Nil(ctx.position, reason="Attempting to get the head of an empty string")
            }

            return Text(str.value.take(1), str.position)
        }
    }

    object TailS : EagerFunction(
        "TailS",
        Types.Text,
        listOf(
            textVar,
        )
    ) {
        override fun apply(interpreter: InterpreterInterface, args: List<Construction>, ctx: FnCallContext): Construction {
            val str = args.first()

            if (str !is Text || str.value.isEmpty()) {
                return Nil(ctx.position, reason="Cannot take the tail of a symbolic value")
            }

            return Text(str.value.drop(1), str.position)
        }
    }

    object LenS : EagerFunction(
        "LenS",
        Types.Int,
        listOf(
            textVar,
        )
    ) {
        override fun apply(interpreter: InterpreterInterface, args: List<Construction>, ctx: FnCallContext): Construction {
            val str = args.first()

            if (str !is Text || str.value.isEmpty()) {
                return Nil(ctx.position, reason="Cannot compute the length of a symbolic string")
            }

            return Integral(str.value.length.toLong(), str.position)
        }
    }
}