package org.fpeterek.tilscript.stdlib.constructions

import org.fpeterek.tilscript.stdlib.Types
import org.fpeterek.tilscript.common.interpreterinterface.DefaultFunction
import org.fpeterek.tilscript.common.interpreterinterface.FnCallContext
import org.fpeterek.tilscript.common.interpreterinterface.InterpreterInterface
import org.fpeterek.tilscript.common.sentence.*
import org.fpeterek.tilscript.common.types.ConstructionType
import org.fpeterek.tilscript.common.types.FunctionType as TilFunctionType
import org.fpeterek.tilscript.common.SrcPosition

object FunctionFunctions {
    private val noPos get() = SrcPosition(-1, -1)

    object CreateFunctionRef : DefaultFunction(
        "CreateFunctionRef",
        ConstructionType,
        listOf(
            Variable("name", noPos, Types.Text),
            Variable("type", noPos, Types.Type),
        ),
    ) {
        override fun apply(interpreter: InterpreterInterface, args: List<Construction>, ctx: FnCallContext): Construction {

            val name = args[0]
            val type = args[0]

            if (name !is Text) {
                return Nil(ctx.position, reason="Function name must not be a symbolic value")
            }

            if (type !is TypeRef || type.type !is TilFunctionType) {
                return Nil(ctx.position, reason="Invalid function type")
            }

            return TilFunction(name.value, type = type.type, srcPosition = ctx.position)
        }

    }

    object FunctionName : DefaultFunction(
        "FunctionName",
        Types.Text,
        listOf(
            Variable("fn", noPos, ConstructionType),
        ),
    ) {
        override fun apply(interpreter: InterpreterInterface, args: List<Construction>, ctx: FnCallContext): Construction {

            val cons = args[0]

            if (cons !is TilFunction) {
                return Nil(ctx.position, reason = "FunctionName expects a function")
            }

            return Text(cons.name, ctx.position)
        }
    }

    object FunctionType : DefaultFunction(
        "FunctionType",
        Types.Type,
        listOf(
            Variable("fn", noPos, ConstructionType),
        ),
    ) {
        override fun apply(interpreter: InterpreterInterface, args: List<Construction>, ctx: FnCallContext): Construction {

            val cons = args[0]

            if (cons !is TilFunction) {
                return Nil(ctx.position, reason = "FunctionType expects a function")
            }

            return TypeRef(cons.constructedType, ctx.position)
        }
    }

    object GetFunction : DefaultFunction(
        "GetFunction",
        ConstructionType,
        listOf(
            Variable("name", noPos, Types.Text),
        ),
    ) {
        override fun apply(interpreter: InterpreterInterface, args: List<Construction>, ctx: FnCallContext): Construction {

            val cons = args[0]

            if (cons !is Text) {
                return Nil(ctx.position, reason = "Function name must not be symbolic")
            }

            return when (val fn = interpreter.getFunction(cons.value)) {
                null -> Nil(ctx.position, reason="Function ${cons.value} does not exist")
                else -> fn
            }
        }
    }
}