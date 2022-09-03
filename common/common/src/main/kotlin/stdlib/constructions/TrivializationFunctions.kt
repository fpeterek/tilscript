package org.fpeterek.tilscript.common.stdlib.constructions

import org.fpeterek.tilscript.common.interpreterinterface.DefaultFunction
import org.fpeterek.tilscript.common.interpreterinterface.FnCallContext
import org.fpeterek.tilscript.common.interpreterinterface.InterpreterInterface
import org.fpeterek.tilscript.common.sentence.Construction
import org.fpeterek.tilscript.common.sentence.Nil
import org.fpeterek.tilscript.common.sentence.Trivialization
import org.fpeterek.tilscript.common.sentence.Variable
import org.fpeterek.tilscript.common.types.ConstructionType
import org.fpeterek.tilscript.common.types.GenericType
import org.fpeterek.tilscript.common.SrcPosition

object TrivializationFunctions {
    private val noPos get() = SrcPosition(-1, -1)

    object Trivialize : DefaultFunction(
        "Trivialize",
        ConstructionType,
        listOf(
            Variable("value", noPos, GenericType(1)),
        ),
    ) {
        override fun apply(interpreter: InterpreterInterface, args: List<Construction>, ctx: FnCallContext) =
            Trivialization(args[0], srcPos = ctx.position)

    }

    object TrivializationBody : DefaultFunction(
        "TrivializationBody",
        ConstructionType,
        listOf(
            Variable("trivialization", noPos, ConstructionType),
        ),
    ) {
        override fun apply(interpreter: InterpreterInterface, args: List<Construction>, ctx: FnCallContext): Construction {

            val cons = args[0]

            if (cons !is Trivialization) {
                return Nil(ctx.position, reason = "TrivializationBody expects a trivialization")
            }

            return cons.construction
        }
    }
}