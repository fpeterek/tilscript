package org.fpeterek.tilscript.stdlib.constructions

import org.fpeterek.tilscript.stdlib.Types
import org.fpeterek.tilscript.common.interpreterinterface.DefaultFunction
import org.fpeterek.tilscript.common.interpreterinterface.FnCallContext
import org.fpeterek.tilscript.common.interpreterinterface.InterpreterInterface
import org.fpeterek.tilscript.common.sentence.*
import org.fpeterek.tilscript.common.types.ConstructionType
import org.fpeterek.tilscript.common.SrcPosition

object VariableFunctions {

    private val noPos get() = SrcPosition(-1, -1)

    object ConsVariable : DefaultFunction(
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

    object GetVariable : DefaultFunction(
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

    object VariableType : DefaultFunction(
        "VariableType",
        Types.Type,
        listOf(
            Variable("name", noPos, ConstructionType),
        ),
    ) {
        override fun apply(interpreter: InterpreterInterface, args: List<Construction>, ctx: FnCallContext): Construction {

            val variable = args[0]

            if (variable !is Variable) {
                return Nil(ctx.position, reason="VariableType function expects a Variable as it's argument")
            }

            return TypeRef(variable.constructedType, srcPos = ctx.position)
        }
    }

    object VariableName : DefaultFunction(
        "VariableName",
        Types.Type,
        listOf(
            Variable("name", noPos, ConstructionType),
        ),
    ) {
        override fun apply(interpreter: InterpreterInterface, args: List<Construction>, ctx: FnCallContext): Construction {

            val variable = args[0]

            if (variable !is Variable) {
                return Nil(ctx.position, reason="VariableName function expects a Variable as it's argument")
            }

            return Text(variable.name, srcPos = ctx.position)
        }
    }
}
