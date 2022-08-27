package org.fpeterek.tilscript.interpreter.interpreter.builtins.constructions

import org.fpeterek.tilscript.interpreter.interpreter.builtins.Types
import org.fpeterek.tilscript.interpreter.interpreter.interpreterinterface.EagerFunction
import org.fpeterek.tilscript.interpreter.interpreter.interpreterinterface.FnCallContext
import org.fpeterek.tilscript.interpreter.interpreter.interpreterinterface.InterpreterInterface
import org.fpeterek.tilscript.interpreter.sentence.*
import org.fpeterek.tilscript.interpreter.sentence.EmptyList
import org.fpeterek.tilscript.interpreter.types.ConstructionType
import org.fpeterek.tilscript.interpreter.types.ListType
import org.fpeterek.tilscript.interpreter.util.SrcPosition


object CompositionFunctions {
    private val noPos get() = SrcPosition(-1, -1)

    object Compose : EagerFunction(
        "Compose",
        ConstructionType,
        listOf(
            Variable("constructions", noPos, ListType(ConstructionType)),
        ),
    ) {
        override fun apply(interpreter: InterpreterInterface, args: List<Construction>, ctx: FnCallContext): Construction {

            val list = args[0]

            if (list !is TilList) {
                return Nil(ctx.position, reason="List of constructions must not be symbolic")
            }

            val constructions = list.toKotlinList()

            if (constructions.isEmpty()) {
                return Nil(ctx.position, reason="Composition must consist of at least a function to apply")
            }

            if (constructions.any { it is Symbol }) {
                return Nil(ctx.position, reason="Composition arguments must not be symbolic")
            }

            return Composition(
                function = constructions.first(),
                args = constructions.drop(1),
                srcPos = ctx.position,
            )
        }

    }

    object CompositionConstructions : EagerFunction(
        "CompositionConstructions",
        ListType(ConstructionType),
        listOf(
            Variable("construction", noPos, ConstructionType),
        ),
    ) {
        override fun apply(interpreter: InterpreterInterface, args: List<Construction>, ctx: FnCallContext): Construction {

            val cons = args[0]

            if (cons !is Composition) {
                return Nil(ctx.position, reason = "CompositionConstructions expects a composition")
            }

            val constructions = cons.args.reversed() + listOf(cons.function)

            return constructions.fold(EmptyList(ConstructionType, ctx.position) as TilList) { acc, value ->
                ListCell(value, acc, ConstructionType, ctx.position)
            }
        }
    }

    object CompositionConstructionAt : EagerFunction(
        "CompositionConstructionAt",
        ConstructionType,
        listOf(
            Variable("construction", noPos, ConstructionType),
            Variable("idx", noPos, Types.Int),
        ),
    ) {
        override fun apply(interpreter: InterpreterInterface, args: List<Construction>, ctx: FnCallContext): Construction {

            val cons = args[0]
            val idx = args[1]

            if (cons !is Composition) {
                return Nil(ctx.position, reason = "CompositionConstructions expects a composition")
            }

            if (idx !is Integral) {
                return Nil(ctx.position, reason = "Index must not be symbolic")
            }

            if (idx.value > cons.args.size || idx.value < 0) {
                return Nil(ctx.position, reason = "Index out of range")
            }

            return when (val index = idx.value.toInt()) {
                0    -> cons.function
                else -> cons.args[index - 1]
            }
        }
    }
}
