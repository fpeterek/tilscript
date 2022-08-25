package org.fpeterek.tilscript.interpreter.interpreter.builtins

import org.fpeterek.tilscript.interpreter.interpreter.interpreterinterface.EagerFunction
import org.fpeterek.tilscript.interpreter.interpreter.interpreterinterface.FnCallContext
import org.fpeterek.tilscript.interpreter.interpreter.interpreterinterface.InterpreterInterface
import org.fpeterek.tilscript.interpreter.sentence.*
import org.fpeterek.tilscript.interpreter.types.ConstructionType
import org.fpeterek.tilscript.interpreter.types.ListType
import org.fpeterek.tilscript.interpreter.types.TupleType
import org.fpeterek.tilscript.interpreter.util.SrcPosition

object ConstructionFunctions {

    private val noPos = SrcPosition(-1, -1)

    object ConsVariable : EagerFunction(
        "ConsVariable",
        ConstructionType,
        listOf(
            Variable("name", noPos, Types.Text),
            Variable("type", noPos, Types.Type),
        ),
    ) {

        override fun apply(interpreter: InterpreterInterface, args: List<Construction>, ctx: FnCallContext): Construction {

            val name = args[0]
            val type = args[1]

            if (name !is Text) {
                return Nil(ctx.position, reason="Variable name cannot be a symbolic value")
            }

            if (type !is TypeRef) {
                return Nil(ctx.position, reason="Variable type cannot be a symbolic value")
            }

            return Variable(name.value, ctx.position, type=type.type)
        }
    }

    object GetVariable : EagerFunction(
        "GetVariable",
        ConstructionType,
        listOf(
            Variable("name", noPos, Types.Text),
        ),
    ) {
        override fun apply(interpreter: InterpreterInterface, args: List<Construction>, ctx: FnCallContext): Construction {

            val name = args[0]

            if (name !is Text) {
                return Nil(ctx.position, reason="Variable name cannot be a symbolic value")
            }

            return interpreter.getVariable(name.value) ?: Nil(ctx.position, reason="Variable '$name' does not exist")
        }
    }

    object ConsClosure : EagerFunction(
        "ConsClosure",
        ConstructionType,
        listOf(
            Variable("vars", noPos, ListType(ConstructionType)),
            Variable("construction", noPos, ConstructionType),
        ),
    ) {
        override fun apply(interpreter: InterpreterInterface, args: List<Construction>, ctx: FnCallContext): Construction {
            TODO("Not yet implemented")
        }
    }

}
































