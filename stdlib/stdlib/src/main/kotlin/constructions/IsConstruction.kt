package org.fpeterek.tilscript.stdlib.constructions

import org.fpeterek.tilscript.stdlib.Types
import org.fpeterek.tilscript.common.interpreterinterface.DefaultFunction
import org.fpeterek.tilscript.common.interpreterinterface.FnCallContext
import org.fpeterek.tilscript.common.interpreterinterface.InterpreterInterface
import org.fpeterek.tilscript.common.sentence.*
import org.fpeterek.tilscript.common.types.ConstructionType
import org.fpeterek.tilscript.common.types.GenericType
import org.fpeterek.tilscript.common.types.ListType
import org.fpeterek.tilscript.common.types.TupleType
import org.fpeterek.tilscript.common.SrcPosition

object IsConstruction {

    private val noPos get() = SrcPosition(-1, -1)

    object IsVariable : DefaultFunction(
        "IsVariable",
        Types.Bool,
        listOf(
            Variable("name", noPos, ConstructionType)
        ),
    ) {
        override fun apply(interpreter: InterpreterInterface, args: List<Construction>, ctx: FnCallContext) =
            Bool(value = args[0] is Variable, noPos)
    }

    object IsComposition : DefaultFunction(
        "IsComposition",
        Types.Bool,
        listOf(
            Variable("name", noPos, ConstructionType)
        ),
    ) {
        override fun apply(interpreter: InterpreterInterface, args: List<Construction>, ctx: FnCallContext) =
            Bool(value = args[0] is Composition, noPos)
    }

    object IsClosure : DefaultFunction(
        "IsClosure",
        Types.Bool,
        listOf(
            Variable("name", noPos, ConstructionType)
        ),
    ) {
        override fun apply(interpreter: InterpreterInterface, args: List<Construction>, ctx: FnCallContext) =
            Bool(value = args[0] is Closure, noPos)
    }

    object IsExecution : DefaultFunction(
        "IsExecution",
        Types.Bool,
        listOf(
            Variable("name", noPos, ConstructionType)
        ),
    ) {
        override fun apply(interpreter: InterpreterInterface, args: List<Construction>, ctx: FnCallContext) =
            Bool(value = args[0] is Execution, noPos)
    }

    object IsFunction : DefaultFunction(
        "IsFunction",
        Types.Bool,
        listOf(
            Variable("name", noPos, GenericType(1))
        ),
    ) {
        override fun apply(interpreter: InterpreterInterface, args: List<Construction>, ctx: FnCallContext) =
            Bool(value = args[0] is TilFunction, noPos)
    }

    object IsTrivialization : DefaultFunction(
        "IsTrivialization",
        Types.Bool,
        listOf(
            Variable("name", noPos, ConstructionType)
        ),
    ) {
        override fun apply(interpreter: InterpreterInterface, args: List<Construction>, ctx: FnCallContext) =
            Bool(value = args[0] is Trivialization, noPos)
    }

    object IsSymbol : DefaultFunction(
        "IsSymbol",
        Types.Bool,
        listOf(
            Variable("name", noPos, GenericType(1))
        ),
    ) {
        override fun apply(interpreter: InterpreterInterface, args: List<Construction>, ctx: FnCallContext) =
            Bool(value = args[0] is Symbol, noPos)
    }

    object IsValue : DefaultFunction(
        "IsValue",
        Types.Bool,
        listOf(
            Variable("name", noPos, GenericType(1))
        ),
    ) {
        override fun apply(interpreter: InterpreterInterface, args: List<Construction>, ctx: FnCallContext) =
            Bool(value = args[0] is Value, noPos)
    }

    object IsList : DefaultFunction(
        "IsList",
        Types.Bool,
        listOf(
            Variable("name", noPos, GenericType(1))
        ),
    ) {
        override fun apply(interpreter: InterpreterInterface, args: List<Construction>, ctx: FnCallContext) =
            Bool(value = args[0].constructedType is ListType, noPos)
    }

    object IsTuple : DefaultFunction(
        "IsTuple",
        Types.Bool,
        listOf(
            Variable("name", noPos, GenericType(1))
        ),
    ) {
        override fun apply(interpreter: InterpreterInterface, args: List<Construction>, ctx: FnCallContext) =
            Bool(value = args[0].constructedType is TupleType, noPos)
    }

    object IsConstruction : DefaultFunction(
        "IsConstruction",
        Types.Bool,
        listOf(
            Variable("name", noPos, GenericType(1))
        ),
    ) {
        override fun apply(interpreter: InterpreterInterface, args: List<Construction>, ctx: FnCallContext) =
            Bool(value = args[0] !is TilList && args[0] !is Symbol && args[0] !is Value && args[0] !is TilFunction &&
                args[0] !is Struct && args[0] !is TilTuple, noPos)
    }

    object IsStruct : DefaultFunction(
        "IsStruct",
        Types.Bool,
        listOf(
            Variable("name", noPos, GenericType(1))
        ),
    ) {
        override fun apply(interpreter: InterpreterInterface, args: List<Construction>, ctx: FnCallContext) =
            Bool(value = args[0] is Struct, noPos)
    }
}