package org.fpeterek.tilscript.interpreter.interpreter.builtins.constructions

import org.fpeterek.tilscript.interpreter.interpreter.builtins.Types
import org.fpeterek.tilscript.interpreter.interpreter.interpreterinterface.DefaultFunction
import org.fpeterek.tilscript.interpreter.interpreter.interpreterinterface.FnCallContext
import org.fpeterek.tilscript.interpreter.interpreter.interpreterinterface.InterpreterInterface
import org.fpeterek.tilscript.interpreter.sentence.*
import org.fpeterek.tilscript.interpreter.types.ConstructionType
import org.fpeterek.tilscript.interpreter.types.GenericType
import org.fpeterek.tilscript.interpreter.util.SrcPosition


object ExecutionFunctions {
    private val noPos get() = SrcPosition(-1, -1)

    object Execute : DefaultFunction(
        "Execute",
        ConstructionType,
        listOf(
            Variable("value", noPos, GenericType(1)),
            Variable("order", noPos, Types.Int),
        ),
    ) {
        override fun apply(interpreter: InterpreterInterface, args: List<Construction>, ctx: FnCallContext): Construction {

            val order = args[1]

            if (order !is Integral) {
                return Nil(ctx.position, reason="Execution order must not be a symbolic value")
            }

            if (order.value !in 1..2) {
                return Nil(ctx.position, reason="Execution order must be 1 or 2")
            }

            return Execution(args[0], executionOrder=order.value.toInt(), srcPos=ctx.position)
        }

    }

    object ExecutionBody : DefaultFunction(
        "ExecutionBody",
        ConstructionType,
        listOf(
            Variable("execution", noPos, ConstructionType),
        ),
    ) {
        override fun apply(interpreter: InterpreterInterface, args: List<Construction>, ctx: FnCallContext): Construction {

            val cons = args[0]

            if (cons !is Execution) {
                return Nil(ctx.position, reason = "ExecutionBody expects a execution")
            }

            return cons.construction
        }
    }

    object ExecutionOrder : DefaultFunction(
        "ExecutionOrder",
        Types.Int,
        listOf(
            Variable("execution", noPos, ConstructionType),
        ),
    ) {
        override fun apply(interpreter: InterpreterInterface, args: List<Construction>, ctx: FnCallContext): Construction {

            val cons = args[0]

            if (cons !is Execution) {
                return Nil(ctx.position, reason = "ExecutionOrder expects a execution")
            }

            return Integral(cons.executionOrder.toLong(), srcPos = ctx.position)
        }
    }
}