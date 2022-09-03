package org.fpeterek.tilscript.stdlib

import org.fpeterek.tilscript.common.interpreterinterface.BuiltinVariadicFunction
import org.fpeterek.tilscript.common.interpreterinterface.DefaultFunction
import org.fpeterek.tilscript.common.interpreterinterface.FnCallContext
import org.fpeterek.tilscript.common.interpreterinterface.InterpreterInterface
import org.fpeterek.tilscript.common.sentence.*
import org.fpeterek.tilscript.common.types.GenericType
import org.fpeterek.tilscript.common.SrcPosition

object TupleFunctions {

    object MkTuple : BuiltinVariadicFunction(
        "MkTuple",
        GenericType(1),
        listOf(
            Variable("placeholder", SrcPosition(-1, -1), GenericType(2))
        ),
        acceptsNil = false
    ) {

        override fun apply(interpreter: InterpreterInterface, args: List<Construction>, ctx: FnCallContext) =
            TilTuple(args, ctx.position)

    }

    object Get : DefaultFunction(
        "Get",
        GenericType(1),
        listOf(
            Variable("tuple", SrcPosition(-1, -1), GenericType(2)),
            Variable("idx", SrcPosition(-1, -1), Types.Int)
        ),
    ) {
        override fun apply(interpreter: InterpreterInterface, args: List<Construction>, ctx: FnCallContext): Construction {
            val tuple = args[0]
            val idx = args[1]

            if (tuple !is TilTuple) {
                return Nil(ctx.position, reason="First argument of Get must be a tuple. (${tuple.constructionType} received)")
            }

            if (idx is Symbol) {
                return Nil(ctx.position, reason="Cannot index a tuple using a symbolic index")
            }

            idx as Integral

            if (idx.value < 0 || idx.value >= tuple.values.size) {
                return Nil(ctx.position, reason="Index out of range")
            }

            return tuple.values[idx.value.toInt()]
        }

    }

    object TupleLen : DefaultFunction(
        "TupleLen",
        Types.Int,
        listOf(
            Variable("tuple", SrcPosition(-1, -1), GenericType(1)),
        ),
    ) {

        override fun apply(interpreter: InterpreterInterface, args: List<Construction>, ctx: FnCallContext): Construction {
            val tuple = args[0]

            if (tuple !is TilTuple) {
                return Nil(ctx.position, reason = "First argument of Get must be a tuple. (${tuple.constructionType} received)")
            }

            return Integral(value = tuple.values.size.toLong(), SrcPosition(-1, -1))
        }

    }

}
