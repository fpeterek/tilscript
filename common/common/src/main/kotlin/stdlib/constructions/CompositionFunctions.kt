package org.fpeterek.tilscript.common.stdlib.constructions

import org.fpeterek.tilscript.common.stdlib.Types
import org.fpeterek.tilscript.common.interpreterinterface.DefaultFunction
import org.fpeterek.tilscript.common.interpreterinterface.FnCallContext
import org.fpeterek.tilscript.common.interpreterinterface.InterpreterInterface
import org.fpeterek.tilscript.common.sentence.*
import org.fpeterek.tilscript.common.sentence.EmptyList
import org.fpeterek.tilscript.common.types.ConstructionType
import org.fpeterek.tilscript.common.types.ListType
import org.fpeterek.tilscript.common.SrcPosition


object CompositionFunctions {
    private val noPos get() = SrcPosition(-1, -1)

    object Compose : DefaultFunction(
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

    object CompositionConstructions : DefaultFunction(
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

    object CompositionConstructionAt : DefaultFunction(
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
