package org.fpeterek.tilscript.stdlib

import org.fpeterek.tilscript.common.interpreterinterface.DefaultFunction
import org.fpeterek.tilscript.common.interpreterinterface.FnCallContext
import org.fpeterek.tilscript.common.interpreterinterface.InterpreterInterface
import org.fpeterek.tilscript.common.sentence.*
import org.fpeterek.tilscript.common.sentence.EmptyList as NilList
import org.fpeterek.tilscript.common.types.GenericType
import org.fpeterek.tilscript.common.types.ListType
import org.fpeterek.tilscript.common.types.Util.isGeneric
import org.fpeterek.tilscript.common.SrcPosition

object ListFunctions {

    object ListOfOne : DefaultFunction(
        "ListOfOne",
        ListType(GenericType(1)),
        listOf(
            Variable("head", SrcPosition(-1, -1), GenericType(1)),
        ),
    ) {

        override fun apply(interpreter: InterpreterInterface, args: List<Construction>, ctx: FnCallContext) = ListCell(
            args[0],
            NilList(args[0].constructedType, ctx.position),
            args[0].constructedType,
            srcPos = ctx.position
        )
    }

    object Cons : DefaultFunction(
        "Cons",
        ListType(GenericType(1)),
        listOf(
            Variable("head", SrcPosition(-1, -1), GenericType(1)),
            Variable("tail", SrcPosition(-1, -1), ListType(GenericType(1))),
        )
    ) {

        override fun apply(interpreter: InterpreterInterface, args: List<Construction>, ctx: FnCallContext): Construction {
            val head = args[0]
            val tail = args[1]

            if (tail is Symbol) {
                return Nil(ctx.position, reason="Cannot construct a new list from a symbolic list")
            }

            if (tail !is TilList) {
                return Nil(ctx.position, reason="List tail must be a list")
            }

            val newTail = when (tail is NilList && tail.valueType.isGeneric) {
                true -> NilList(head.constructionType, tail.position)
                else -> tail
            }

            if (!interpreter.typesMatch(head.constructionType, newTail.valueType)) {
                return Nil(ctx.position, reason="Lists must be homogeneous (head type (${head.constructionType}) does not match list value type (${newTail.valueType}))")
            }

            return ListCell(
                head, newTail, newTail.valueType, head.position
            )
        }

    }

    object Head : DefaultFunction(
        "Head",
        GenericType(1),
        listOf(
            Variable("list", SrcPosition(-1, -1), ListType(GenericType(1))),
        ),
    ) {
        override fun apply(interpreter: InterpreterInterface, args: List<Construction>, ctx: FnCallContext) = when (args[0]) {
            is TilList -> (args[0] as TilList).let { list ->
                when (list) {
                    is NilList -> Nil(
                        ctx.position,
                        reason = "Empty list has no head"
                    )

                    is ListCell -> list.head
                }
            }

            else -> Nil(ctx.position, reason="Cannot take the head of a symbolic List")
        }
    }

    object Tail : DefaultFunction(
        "Tail",
        ListType(GenericType(1)),
        listOf(
            Variable("list", SrcPosition(-1, -1), ListType(GenericType(1))),
        ),
    ) {
        override fun apply(interpreter: InterpreterInterface, args: List<Construction>, ctx: FnCallContext) = when (args[0]) {
            is TilList -> (args[0] as TilList).let { list ->
                when (list) {
                    is NilList  -> Nil(ctx.position, reason = "Empty list has no tail")
                    is ListCell -> list.tail
                }
            }

            else -> Nil(ctx.position, reason="Cannot take the tail of a symbolic List")
        }
    }

    object IsEmpty : DefaultFunction(
        "IsEmpty",
        Types.Bool,
        listOf(
            Variable("list", SrcPosition(-1, -1), ListType(GenericType(1))),
        ),
    ) {
        override fun apply(interpreter: InterpreterInterface, args: List<Construction>, ctx: FnCallContext) = when (args[0]) {
            is TilList -> Bool(args[0] is NilList, ctx.position)
            else       -> Nil(ctx.position, reason="Cannot determine the contents of a symbolic List")
        }
    }

    object EmptyListOf : DefaultFunction(
        "EmptyListOf",
        ListType(GenericType(1)),
        listOf(
            Variable("type", SrcPosition(-1, -1), Types.Type),
        ),
    ) {
        override fun apply(interpreter: InterpreterInterface, args: List<Construction>, ctx: FnCallContext) = when (args[0]) {
            is TypeRef -> NilList((args[0] as TypeRef).type, ctx.position)
            else -> Nil(
                ctx.position,
                reason="Cannot construct an empty list of a non-concrete type"
            )
        }
    }
}
