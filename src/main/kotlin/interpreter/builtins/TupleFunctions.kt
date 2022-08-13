package org.fpeterek.til.interpreter.interpreter.builtins

import org.fpeterek.til.interpreter.interpreter.interpreterinterface.BuiltinBareFunction
import org.fpeterek.til.interpreter.interpreter.interpreterinterface.EagerFunction
import org.fpeterek.til.interpreter.interpreter.interpreterinterface.InterpreterInterface
import org.fpeterek.til.interpreter.sentence.*
import org.fpeterek.til.interpreter.types.GenericType
import org.fpeterek.til.interpreter.util.SrcPosition

object TupleFunctions {

    object MkTuple : BuiltinBareFunction(
        "MkTuple",
        GenericType(1),
        listOf(
            Variable("placeholder", SrcPosition(-1, -1), GenericType(2))
        )
    ) {

        override fun apply(interpreter: InterpreterInterface, args: List<Construction>): Construction {
            val int = args.map(interpreter::interpret)

            val nil = int.firstOrNull { it is Nil }

            if (nil != null) {
                return nil
            }

            return TilTuple(
                int,
                int.firstOrNull()?.position ?: SrcPosition(-1, -1),
            )
        }

    }

    object Get : EagerFunction(
        "Get",
        GenericType(1),
        listOf(
            Variable("tuple", SrcPosition(-1, -1), GenericType(2)),
            Variable("idx", SrcPosition(-1, -1), Types.Int)
        ),
    ) {
        override fun apply(interpreter: InterpreterInterface, args: List<Construction>): Construction {
            val tuple = args[0]
            val idx = args[1]

            if (tuple !is TilTuple) {
                throw RuntimeException("First argument of Get must be a tuple. (${tuple.constructionType} received)")
            }

            if (idx is Symbol) {
                return Values.Nil
            }

            idx as Integral

            if (idx.value < 0 || idx.value >= tuple.values.size) {
                throw RuntimeException("Index out of range")
            }

            return tuple.values[idx.value.toInt()]
        }

    }

}
