package org.fpeterek.til.typechecking.interpreter.builtins

import org.fpeterek.til.typechecking.interpreter.interpreterinterface.EagerFunction
import org.fpeterek.til.typechecking.interpreter.interpreterinterface.InterpreterInterface
import org.fpeterek.til.typechecking.interpreter.interpreterinterface.LazyFunction
import org.fpeterek.til.typechecking.sentence.*
import org.fpeterek.til.typechecking.tilscript.Builtins
import org.fpeterek.til.typechecking.types.ConstructionType
import org.fpeterek.til.typechecking.types.GenericType
import org.fpeterek.til.typechecking.types.ListType
import org.fpeterek.til.typechecking.util.SrcPosition

object Util {


    object Print : LazyFunction(
        "Print",
        Builtins.Bool,
        listOf(
            Variable("arg", SrcPosition(-1, -1), GenericType(1)),
        )
    ) {
        override fun apply(interpreter: InterpreterInterface, args: List<Construction>): Construction {
            val arg = interpreter.interpret(args.first())
            print(arg)
            return Builtins.True
        }
    }

    object Println : LazyFunction(
        "Println",
        Builtins.Bool,
        listOf(
            Variable("arg", SrcPosition(-1, -1), GenericType(1)),
        )
    ) {
        override fun apply(interpreter: InterpreterInterface, args: List<Construction>): Construction {
            Print.apply(interpreter, args)
            println()
            return Builtins.True
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
        override fun apply(interpreter: InterpreterInterface, args: List<Construction>): Construction {
            return args.last()
        }
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
                return Builtins.Nil
            }

            var cell = list as ListCell

            while (cell.tail !is EmptyList) {
                cell = cell.tail as ListCell
            }

            return cell.head
        }
    }

}
