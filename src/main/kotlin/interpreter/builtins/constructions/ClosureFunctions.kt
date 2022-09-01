package org.fpeterek.tilscript.interpreter.interpreter.builtins.constructions

import org.fpeterek.tilscript.interpreter.interpreter.builtins.Types
import org.fpeterek.tilscript.interpreter.interpreter.interpreterinterface.DefaultFunction
import org.fpeterek.tilscript.interpreter.interpreter.interpreterinterface.FnCallContext
import org.fpeterek.tilscript.interpreter.interpreter.interpreterinterface.InterpreterInterface
import org.fpeterek.tilscript.interpreter.sentence.*
import org.fpeterek.tilscript.interpreter.sentence.EmptyList
import org.fpeterek.tilscript.interpreter.types.ConstructionType
import org.fpeterek.tilscript.interpreter.types.ListType
import org.fpeterek.tilscript.interpreter.util.SrcPosition

object ClosureFunctions {

    private val noPos get() = SrcPosition(-1, -1)

    object ConsClosure : DefaultFunction(
        "ConsClosure",
        ConstructionType,
        listOf(
            Variable("vars", noPos, ListType(ConstructionType)),
            Variable("construction", noPos, ConstructionType),
            Variable("returns", noPos, Types.Type),
        ),
    ) {
        override fun apply(interpreter: InterpreterInterface, args: List<Construction>, ctx: FnCallContext): Construction {

            val cons = args[0]
            val vars = args[1]
            val returns = args[2]

            if (cons is Symbol || vars !is TilList || returns !is TypeRef) {
                return Nil(ctx.position, reason = "A closure cannot be constructed from symbolic values")
            }

            val varList = vars.toKotlinList()

            val symbol = varList.firstOrNull { it is Symbol }

            if (symbol != null) {
                return Nil(ctx.position, reason = "A closure cannot be constructed from symbolic values")
            }

            return Closure(varList.map { it as Variable }, cons, returns.type, ctx.position)
        }
    }

    object ClosureArgs : DefaultFunction(
        "ClosureArgs",
        ListType(ConstructionType),
        listOf(
            Variable("closure", noPos, ConstructionType),
        ),
    ) {
        override fun apply(interpreter: InterpreterInterface, args: List<Construction>, ctx: FnCallContext): Construction {

            val cons = args[0]

            if (cons !is Closure) {
                return Nil(ctx.position, reason = "ClosureArgs expects a closure")
            }

            return cons.variables.foldRight(EmptyList(ConstructionType, ctx.position) as TilList) { variable, acc ->
                ListCell(variable, acc, ConstructionType, ctx.position)
            }
        }
    }

    object ClosureBody : DefaultFunction(
        "ClosureBody",
        ConstructionType,
        listOf(
            Variable("closure", noPos, ConstructionType),
        ),
    ) {
        override fun apply(interpreter: InterpreterInterface, args: List<Construction>, ctx: FnCallContext): Construction {

            val cons = args[0]

            if (cons !is Closure) {
                return Nil(ctx.position, reason = "ClosureBody expects a closure")
            }

            return cons.construction
        }
    }

    object ClosureReturnType : DefaultFunction(
        "ClosureReturnType",
        Types.Type,
        listOf(
            Variable("closure", noPos, ConstructionType),
        ),
    ) {
        override fun apply(interpreter: InterpreterInterface, args: List<Construction>, ctx: FnCallContext): Construction {

            val cons = args[0]

            if (cons !is Closure) {
                return Nil(ctx.position, reason = "ClosureReturnType expects a closure")
            }

            return TypeRef(cons.returnType, ctx.position)
        }
    }
}