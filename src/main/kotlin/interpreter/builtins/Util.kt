package org.fpeterek.tilscript.interpreter.interpreter.builtins

import org.fpeterek.tilscript.interpreter.interpreter.interpreterinterface.*
import org.fpeterek.tilscript.interpreter.sentence.*
import org.fpeterek.tilscript.interpreter.types.ConstructionType
import org.fpeterek.tilscript.interpreter.types.GenericType
import org.fpeterek.tilscript.interpreter.util.SrcPosition
import org.fpeterek.tilscript.interpreter.util.die

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

    object Cond : BuiltinBareFunction(
        "Cond",
        GenericType(1),
        listOf(
            Variable("placeholder", SrcPosition(-1, -1), GenericType(1)),
        )
    ) {
        override fun apply(interpreter: InterpreterInterface, args: List<Construction>, ctx: FnCallContext): Construction {

            interpreter.createLocal(Variable("else", ctx.position), Bool(true, ctx.position))

            if (args.size % 2 != 0) {
                return Nil(ctx.position, reason="Cond expects an even number of arguments")
            }

            var i = 0

            while (i < args.size) {

                val cond = interpreter.interpret(args[i])

                if (cond is Nil) {
                    return cond
                }

                if (!interpreter.typesMatch(cond.constructionType, Types.Bool)) {
                    die("Condition must be a Bool (received: ${cond.constructionType})")
                }

                if (cond !is Bool) {
                    return Nil(ctx.position, reason="Condition must not be symbolic")
                }

                if (cond.value) {
                    return interpreter.interpret(args[i+1])
                }

                i += 2
            }

            return Nil(ctx.position, reason="No condition matched")
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

    object IsNil : LazyFunction(
        "IsNil",
        Types.Bool,
        listOf(
            Variable("arg", SrcPosition(-1, -1), GenericType(1))
        )
    ) {
        override fun apply(interpreter: InterpreterInterface, args: List<Construction>, ctx: FnCallContext) =
            Bool(value = interpreter.interpret(args[0]) is Nil, srcPos = ctx.position)
    }
}
