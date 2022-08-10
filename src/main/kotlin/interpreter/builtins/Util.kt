package org.fpeterek.til.interpreter.interpreter.builtins

import org.fpeterek.til.interpreter.interpreter.interpreterinterface.EagerFunction
import org.fpeterek.til.interpreter.interpreter.interpreterinterface.InterpreterInterface
import org.fpeterek.til.interpreter.interpreter.interpreterinterface.LazyFunction
import org.fpeterek.til.interpreter.sentence.*
import org.fpeterek.til.interpreter.types.ConstructionType
import org.fpeterek.til.interpreter.types.GenericType
import org.fpeterek.til.interpreter.types.ListType
import org.fpeterek.til.interpreter.util.SrcPosition

object Util {

    object Print : LazyFunction(
        "Print",
        Types.Bool,
        listOf(
            Variable("arg", SrcPosition(-1, -1), GenericType(1)),
        )
    ) {
        override fun apply(interpreter: InterpreterInterface, args: List<Construction>): Construction {
            val arg = interpreter.interpret(args.first())
            print(arg)
            return Values.True
        }
    }

    object Println : LazyFunction(
        "Println",
        Types.Bool,
        listOf(
            Variable("arg", SrcPosition(-1, -1), GenericType(1)),
        )
    ) {
        override fun apply(interpreter: InterpreterInterface, args: List<Construction>): Construction {
            Print.apply(interpreter, args)
            println()
            return Values.True
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
        override fun apply(interpreter: InterpreterInterface, args: List<Construction>): Construction =
            when (val cond = interpreter.interpret(args.first())) {
                is Bool -> when (cond.value) {
                    true -> interpreter.interpret(args[1])
                    else -> interpreter.interpret(args[2])
                }
                else -> Values.Nil
            }
    }

    object Chain : EagerFunction(
        "Chain",
        GenericType(2),
        listOf(
            Variable("ignored", SrcPosition(-1, -1), GenericType(1)),
            Variable("returned", SrcPosition(-1, -1), GenericType(2)),
        )
    ) {
        override fun apply(interpreter: InterpreterInterface, args: List<Construction>) = args.last()
    }

    object RunAll : EagerFunction(
        "RunAll",
        GenericType(2),
        listOf(
            Variable("constructions", SrcPosition(-1, -1), ListType(ConstructionType)),
        )
    ) {
        override fun apply(interpreter: InterpreterInterface, args: List<Construction>): Construction {

            val list = args.first() as TilList

            if (list is EmptyList) {
                return Values.Nil
            }

            var cell = list as ListCell

            while (cell.tail !is EmptyList) {
                cell = cell.tail as ListCell
            }

            return cell.head
        }
    }

}
