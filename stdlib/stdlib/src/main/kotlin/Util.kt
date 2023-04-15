package org.fpeterek.tilscript.stdlib

import org.fpeterek.tilscript.common.interpreterinterface.*
import org.fpeterek.tilscript.common.sentence.*
import org.fpeterek.tilscript.common.types.ConstructionType
import org.fpeterek.tilscript.common.types.GenericType
import org.fpeterek.tilscript.common.SrcPosition
import org.fpeterek.tilscript.common.die
import org.fpeterek.tilscript.common.types.TupleType
import kotlin.system.exitProcess

import kotlin.random.Random as KtRandom

object Util {

    object Print : NilAcceptingFunction(
        "Print",
        GenericType(1),
        listOf(
            Variable("arg", SrcPosition(-1, -1), GenericType(1)),
        )
    ) {
        override fun apply(interpreter: InterpreterInterface, args: List<Construction>, ctx: FnCallContext) =
            args.first().apply {

                val str = when (this) {
                    is Text -> value
                    else -> toString()
                }
                print(str)
            }
    }

    object Println : NilAcceptingFunction(
        "Println",
        GenericType(1),
        listOf(
            Variable("arg", SrcPosition(-1, -1), GenericType(1)),
        )
    ) {
        override fun apply(interpreter: InterpreterInterface, args: List<Construction>, ctx: FnCallContext) =
            Print.apply(interpreter, args, ctx).apply {
                println()
            }
    }

    object If : DefaultFunction(
        "If",
        GenericType(1),
        listOf(
            Variable("cond", SrcPosition(-1, -1), Types.Bool),
            Variable("ifTrue", SrcPosition(-1, -1), GenericType(1)),
            Variable("ifFalse", SrcPosition(-1, -1), GenericType(1)),
        ),
    ) {
        override fun apply(interpreter: InterpreterInterface, args: List<Construction>, ctx: FnCallContext): Construction {
            die("If should never be invoked directly, instead, handling of If should be done by the interpreter", ctx.position)
        }
    }

    object Cond : DefaultFunction(
        "Cond",
        GenericType(1),
        listOf(
            Variable("cond", SrcPosition(-1, -1), Types.Bool),
            Variable("ifTrue", SrcPosition(-1, -1), GenericType(1)),
            Variable("ifFalse", SrcPosition(-1, -1), GenericType(1)),
        ),
    ) {
        override fun apply(interpreter: InterpreterInterface, args: List<Construction>, ctx: FnCallContext): Construction {
            die("Cond should never be invoked", ctx.position)
        }
    }

    object Progn : DefaultFunction(
        "Progn",
        GenericType(2),
        listOf(
            Variable("fst", SrcPosition(-1, -1), GenericType(1)),
            Variable("snd", SrcPosition(-1, -1), GenericType(2)),
        ),
    ) {
        override fun apply(interpreter: InterpreterInterface, args: List<Construction>, ctx: FnCallContext) =
            args.last()
    }

    object Tr : DefaultFunction(
        "Tr",
        ConstructionType,
        listOf(
            Variable("tr", SrcPosition(-1, -1), GenericType(1))
        )
    ) {
        override fun apply(interpreter: InterpreterInterface, args: List<Construction>, ctx: FnCallContext) =
            Trivialization(construction = args[0], srcPos = ctx.position, constructedType = args[0].constructionType)
    }

    object TypeOf : DefaultFunction(
        "TypeOf",
        Types.Type,
        listOf(
            Variable("arg", SrcPosition(-1, -1), GenericType(1))
        )
    ) {
        override fun apply(interpreter: InterpreterInterface, args: List<Construction>, ctx: FnCallContext) =
            TypeRef(args[0].constructionType, ctx.position)
    }

    object IsNil : NilAcceptingFunction(
        "IsNil",
        Types.Bool,
        listOf(
            Variable("arg", SrcPosition(-1, -1), GenericType(1))
        )
    ) {
        override fun apply(interpreter: InterpreterInterface, args: List<Construction>, ctx: FnCallContext) =
            Bool(value = args[0] is Nil, srcPos = ctx.position)
    }

    object Exit : DefaultFunction(
        "Exit",
        Types.Int,
        listOf(
            Variable("arg", SrcPosition(-1, -1), Types.Int)
        )
    ) {
        override fun apply(interpreter: InterpreterInterface, args: List<Construction>, ctx: FnCallContext): Construction {
            val arg = args.first()

            if (arg !is Integral) {
                die("Argument of Exit must not be symbolic")
            }

            exitProcess(arg.value.toInt())
        }
    }

    object RandomInt : DefaultFunction(
        "RandomInt",
        Types.Int,
        listOf(
            Variable("ds", srcPos = SrcPosition(-1, -1), type = Types.DeviceState)
        ),
    ) {
        override fun apply(interpreter: InterpreterInterface, args: List<Construction>, ctx: FnCallContext) =
            Integral(value = KtRandom.nextLong(), srcPos = ctx.position)
    }

    object Random : DefaultFunction(
        "Random",
        Types.Real,
        listOf(
            Variable("ds", srcPos = SrcPosition(-1, -1), type = Types.DeviceState)
        ),
    ) {
        override fun apply(interpreter: InterpreterInterface, args: List<Construction>, ctx: FnCallContext) =
            Real(value = KtRandom.nextDouble(), srcPos = ctx.position)
    }

    object NilAt : DefaultFunction(
        "NilAt",
        Types.Int,
        listOf(
            Variable("reason", srcPos = SrcPosition(-1, -1), type = Types.Text),
            Variable("callsite", srcPos = SrcPosition(-1, -1), type = TupleType(Types.Text, Types.Int, Types.Int)),
        ),
    ) {
        override fun apply(interpreter: InterpreterInterface, args: List<Construction>, ctx: FnCallContext): Construction {
            val reason = args.first()
            val callsite = args[1]

            if (reason !is Text || callsite !is TilTuple) {
                die("NilAt arguments must not be symbolic")
            }

            val file = (callsite.values[0] as Text).value
            val line = (callsite.values[1] as Integral).value.toInt()
            val char = (callsite.values[2] as Integral).value.toInt()

            return Nil(SrcPosition(line, char, file), reason=reason.value)
        }
    }
}
