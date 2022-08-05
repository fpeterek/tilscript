package org.fpeterek.til.typechecking.interpreter.builtins

import org.fpeterek.til.typechecking.interpreter.interpreterinterface.EagerFunction
import org.fpeterek.til.typechecking.interpreter.interpreterinterface.InterpreterInterface
import org.fpeterek.til.typechecking.interpreter.interpreterinterface.LazyFunction
import org.fpeterek.til.typechecking.sentence.*
import org.fpeterek.til.typechecking.sentence.EmptyList
import org.fpeterek.til.typechecking.tilscript.Builtins
import org.fpeterek.til.typechecking.types.GenericType
import org.fpeterek.til.typechecking.types.ListType
import org.fpeterek.til.typechecking.util.SrcPosition

object ListFunctions {

    object Cons : LazyFunction(
        "Cons",
        ListType(GenericType(1)),
        listOf(
            Variable("head", SrcPosition(-1, -1), GenericType(1)),
            Variable("tail", SrcPosition(-1, -1), ListType(GenericType(1))),
        )
    ) {

        override fun apply(interpreter: InterpreterInterface, args: List<Construction>): Construction {
            val head = interpreter.interpret(args[0])
            val tail = interpreter.interpret(args[1])

            if (head is Nil) {
                return interpreter.nil
            }

            if (tail is Nil) {
                return ListCell(
                    head, EmptyList(head.constructionType, head.position), head.constructionType, head.position
                )
            }

            if (tail !is TilList) {
                throw RuntimeException("List tail must be a list")
            }

            if (!interpreter.typesMatch(head.constructionType, tail.valueType)) {
                throw RuntimeException("Lists must be homogeneous (head type (${head.constructionType}) does not match list value type (${tail.valueType}))")
            }

            return ListCell(
                head, tail, tail.valueType, head.position
            )
        }

    }

    object Head : EagerFunction(
        "Head",
        GenericType(1),
        listOf(
            Variable("list", SrcPosition(-1, -1), ListType(GenericType(1))),
        ),
    ) {
        override fun apply(interpreter: InterpreterInterface, args: List<Construction>) = (args[0] as TilList).let { list ->
            when (list) {
                is EmptyList -> interpreter.nil
                is ListCell -> list.head
            }
        }
    }

    object Tail : EagerFunction(
        "Tail",
        GenericType(1),
        listOf(
            Variable("list", SrcPosition(-1, -1), ListType(GenericType(1))),
        ),
    ) {
        override fun apply(interpreter: InterpreterInterface, args: List<Construction>) = (args[0] as TilList).let { list ->
            when (list) {
                is EmptyList -> interpreter.nil
                is ListCell -> list.tail
            }
        }
    }

    object IsEmpty : EagerFunction(
        "IsEmpty",
        GenericType(1),
        listOf(
            Variable("list", SrcPosition(-1, -1), ListType(GenericType(1))),
        ),
    ) {
        override fun apply(interpreter: InterpreterInterface, args: List<Construction>) = (args[0] as TilList).let { list ->
            when (list) {
                is EmptyList -> Builtins.True
                is ListCell -> Builtins.False
            }
        }
    }

    object EmptyListOf : EagerFunction(
        "EmptyListOf",
        ListType(GenericType(1)),
        listOf(
            Variable("type", SrcPosition(-1, -1), Builtins.Type),
        ),
    ) {
        override fun apply(interpreter: InterpreterInterface, args: List<Construction>) = (args[0] as TypeRef).let { type ->
            EmptyList(type.type, type.position)
        }
    }

}
