package org.fpeterek.tilscript.interpreter.interpreter.builtins

import org.fpeterek.tilscript.interpreter.interpreter.interpreterinterface.*
import org.fpeterek.tilscript.interpreter.sentence.*
import org.fpeterek.tilscript.interpreter.types.ConstructionType
import org.fpeterek.tilscript.interpreter.types.GenericType
import org.fpeterek.tilscript.interpreter.types.ListType
import org.fpeterek.tilscript.interpreter.types.Type
import org.fpeterek.tilscript.interpreter.util.SrcPosition

object Util {

    object Print : LazyFunction(
        "Print",
        GenericType(1),
        listOf(
            Variable("arg", SrcPosition(-1, -1), GenericType(1)),
        )
    ) {
        override fun apply(interpreter: InterpreterInterface, args: List<Construction>, ctx: FnCallContext) =
            interpreter.interpret(args.first()).apply {

            val str = when (this) {
                is Text -> value
                else -> toString()
            }
            print(str)
        }
    }

    object Println : LazyFunction(
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

    object If : LazyFunction(
        "If",
        GenericType(1),
        listOf(
            Variable("cond", SrcPosition(-1, -1), Types.Bool),
            Variable("ignored", SrcPosition(-1, -1), GenericType(1)),
            Variable("returned", SrcPosition(-1, -1), GenericType(1)),
        )
    ) {
        override fun apply(interpreter: InterpreterInterface, args: List<Construction>, ctx: FnCallContext): Construction =
            when (val cond = interpreter.interpret(args.first())) {
                is Bool -> when (cond.value) {
                    true -> interpreter.interpret(args[1])
                    else -> interpreter.interpret(args[2])
                }
                else -> Nil(ctx.position, reason="If condition cannot be a symbolic value")
            }
    }

    object Progn : BuiltinBareFunction(
        "Progn",
        GenericType(1),
        listOf(
            Variable("placeholder", SrcPosition(-1, -1), GenericType(1)),
        )
    ) {
        override fun apply(interpreter: InterpreterInterface, args: List<Construction>, ctx: FnCallContext): Construction {

            for ((idx, arg) in args.withIndex()) {
                val int = interpreter.interpret(arg)

                if (int is Nil || idx == args.lastIndex) {
                    return int
                }
            }

            return Nil(ctx.position, reason="No arguments were passed to Progn")
        }
    }

    object Tr : EagerFunction(
        "Tr",
        ConstructionType,
        listOf(
            Variable("tr", SrcPosition(-1, -1), GenericType(1))
        )
    ) {
        override fun apply(interpreter: InterpreterInterface, args: List<Construction>, ctx: FnCallContext) =
            Trivialization(construction = args[0], srcPos = ctx.position, constructedType = args[0].constructionType)
    }

    object TypeOf : EagerFunction(
        "TypeOf",
        Types.Type,
        listOf(
            Variable("arg", SrcPosition(-1, -1), GenericType(1))
        )
    ) {
        override fun apply(interpreter: InterpreterInterface, args: List<Construction>, ctx: FnCallContext) =
            TypeRef(args[0].constructionType, ctx.position)
    }

}
