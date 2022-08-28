package org.fpeterek.tilscript.interpreter.interpreter.builtins.constructions

import org.fpeterek.tilscript.interpreter.interpreter.interpreterinterface.EagerFunction
import org.fpeterek.tilscript.interpreter.interpreter.interpreterinterface.FnCallContext
import org.fpeterek.tilscript.interpreter.interpreter.interpreterinterface.InterpreterInterface
import org.fpeterek.tilscript.interpreter.sentence.Construction
import org.fpeterek.tilscript.interpreter.sentence.Nil
import org.fpeterek.tilscript.interpreter.sentence.Trivialization
import org.fpeterek.tilscript.interpreter.sentence.Variable
import org.fpeterek.tilscript.interpreter.types.ConstructionType
import org.fpeterek.tilscript.interpreter.types.GenericType
import org.fpeterek.tilscript.interpreter.util.SrcPosition

object TrivializationFunctions {
    private val noPos get() = SrcPosition(-1, -1)

    object Trivialize : EagerFunction(
        "Trivialize",
        ConstructionType,
        listOf(
            Variable("value", noPos, GenericType(1)),
        ),
    ) {
        override fun apply(interpreter: InterpreterInterface, args: List<Construction>, ctx: FnCallContext) =
            Trivialization(args[0], srcPos = ctx.position)

    }

    object TrivializationBody : EagerFunction(
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