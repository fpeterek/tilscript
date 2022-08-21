package org.fpeterek.tilscript.interpreter.interpreter.builtins

import org.fpeterek.tilscript.interpreter.interpreter.interpreterinterface.EagerFunction
import org.fpeterek.tilscript.interpreter.interpreter.interpreterinterface.FnCallContext
import org.fpeterek.tilscript.interpreter.interpreter.interpreterinterface.InterpreterInterface
import org.fpeterek.tilscript.interpreter.sentence.*
import org.fpeterek.tilscript.interpreter.util.SrcPosition

object Conversions {

    object TextToInt : EagerFunction (
        "TextToInt",
        Types.Int,
        listOf(
            Variable("text", SrcPosition(-1, -1), Types.Text)
        )
    ) {
        override fun apply(interpreter: InterpreterInterface, args: List<Construction>, ctx: FnCallContext): Construction =
            when (args[0]) {
                is Text -> {
                    val text = args[0] as Text

                    when (val conv = text.value.toLongOrNull()) {
                        null -> Nil(reason = "String '${text.value}' could not be converted to an Int", srcPos = ctx.position)
                        else -> Integral(value = conv, srcPos = ctx.position)
                    }
                }

                else -> Nil(reason = "Cannot convert a symbolic value", srcPos = ctx.position)
            }
    }

    object TextToReal : EagerFunction (
        "TextToReal",
        Types.Real,
        listOf(
            Variable("text", SrcPosition(-1, -1), Types.Text)
        )
    ) {
        override fun apply(interpreter: InterpreterInterface, args: List<Construction>, ctx: FnCallContext): Construction =
            when (args[0]) {
                is Text -> {
                    val text = args[0] as Text

                    when (val conv = text.value.toDoubleOrNull()) {
                        null -> Nil(reason = "String '${text.value}' could not be converted to a Real", srcPos = ctx.position)
                        else -> Real(value = conv, srcPos = ctx.position)
                    }
                }

                else -> Nil(reason = "Cannot convert a symbolic value", srcPos = ctx.position)
            }
    }

    object IntToText : EagerFunction (
        "IntToText",
        Types.Text,
        listOf(
            Variable("int", SrcPosition(-1, -1), Types.Int)
        )
    ) {
        override fun apply(interpreter: InterpreterInterface, args: List<Construction>, ctx: FnCallContext): Construction =
            when (args[0]) {
                is Integral -> Text(value = (args[0] as Integral).value.toString(), srcPos = ctx.position)

                else -> Nil(reason = "Cannot convert a symbolic value", srcPos = ctx.position)
            }
    }

    object RealToText : EagerFunction (
        "RealToText",
        Types.Text,
        listOf(
            Variable("real", SrcPosition(-1, -1), Types.Real)
        )
    ) {
        override fun apply(interpreter: InterpreterInterface, args: List<Construction>, ctx: FnCallContext): Construction =
            when (args[0]) {
                is Real -> Text(value = (args[0] as Real).value.toString(), srcPos = ctx.position)

                else -> Nil(reason = "Cannot convert a symbolic value", srcPos = ctx.position)
            }
    }

    object ToWorld : EagerFunction (
        "ToWorld",
        Types.World,
        listOf(
            Variable("int", SrcPosition(-1, -1), Types.Int)
        )
    ) {
        override fun apply(interpreter: InterpreterInterface, args: List<Construction>, ctx: FnCallContext): Construction =
            when (args[0]) {
                is Integral -> World(world = (args[0] as Integral).value, srcPos = ctx.position)

                else -> Nil(reason = "Cannot convert a symbolic value", srcPos = ctx.position)
            }
    }

    object ToTime : EagerFunction (
        "ToTime",
        Types.Time,
        listOf(
            Variable("int", SrcPosition(-1, -1), Types.Int)
        )
    ) {
        override fun apply(interpreter: InterpreterInterface, args: List<Construction>, ctx: FnCallContext): Construction =
            when (args[0]) {
                is Integral -> Timestamp(time = (args[0] as Integral).value, srcPos = ctx.position)

                else -> Nil(reason = "Cannot convert a symbolic value", srcPos = ctx.position)
            }
    }

    object WorldToInt : EagerFunction (
        "WorldToInt",
        Types.Int,
        listOf(
            Variable("arg", SrcPosition(-1, -1), Types.World)
        )
    ) {
        override fun apply(interpreter: InterpreterInterface, args: List<Construction>, ctx: FnCallContext): Construction =
            when (args[0]) {
                is World -> Integral(value = (args[0] as World).world, srcPos = ctx.position)

                else -> Nil(reason = "Cannot convert a symbolic value", srcPos = ctx.position)
            }
    }

    object TimeToInt : EagerFunction (
        "TimeToInt",
        Types.Int,
        listOf(
            Variable("arg", SrcPosition(-1, -1), Types.Time)
        )
    ) {
        override fun apply(interpreter: InterpreterInterface, args: List<Construction>, ctx: FnCallContext): Construction =
            when (args[0]) {
                is Timestamp -> Integral(value = (args[0] as Timestamp).time, srcPos = ctx.position)

                else -> Nil(reason = "Cannot convert a symbolic value", srcPos = ctx.position)
            }
    }
}
