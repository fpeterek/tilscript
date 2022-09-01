package org.fpeterek.tilscript.interpreter.interpreter.builtins

import org.fpeterek.tilscript.interpreter.interpreter.interpreterinterface.*
import org.fpeterek.tilscript.interpreter.sentence.*
import org.fpeterek.tilscript.interpreter.types.ConstructionType
import org.fpeterek.tilscript.interpreter.types.GenericType
import org.fpeterek.tilscript.interpreter.util.SrcPosition
import org.fpeterek.tilscript.interpreter.util.die

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

    object If : BuiltinVariadicFunction(
        "If",
        GenericType(1),
        listOf(
            Variable("cond", SrcPosition(-1, -1), Types.Bool),
            Variable("value", SrcPosition(-1, -1), GenericType(1)),
        ),
        acceptsNil = true
    ) {
        override fun apply(interpreter: InterpreterInterface, args: List<Construction>, ctx: FnCallContext): Construction {
            die("If should never be invoked directly, instead, handling of If should be done by the interpreter")
        }
    }

    object Progn : BuiltinVariadicFunction(
        "Progn",
        GenericType(1),
        listOf(
            Variable("placeholder", SrcPosition(-1, -1), GenericType(1)),
        ),
        acceptsNil = false
    ) {
        override fun apply(interpreter: InterpreterInterface, args: List<Construction>, ctx: FnCallContext) =
            when (args.isNotEmpty()) {
                true -> args.last()
                else -> Nil(ctx.position, reason="No arguments were passed to Progn")
            }
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
}
