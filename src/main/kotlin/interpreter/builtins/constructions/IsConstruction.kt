package org.fpeterek.tilscript.interpreter.interpreter.builtins.constructions

import org.fpeterek.tilscript.interpreter.interpreter.builtins.Types
import org.fpeterek.tilscript.interpreter.interpreter.interpreterinterface.DefaultFunction
import org.fpeterek.tilscript.interpreter.interpreter.interpreterinterface.FnCallContext
import org.fpeterek.tilscript.interpreter.interpreter.interpreterinterface.InterpreterInterface
import org.fpeterek.tilscript.interpreter.sentence.*
import org.fpeterek.tilscript.interpreter.types.ConstructionType
import org.fpeterek.tilscript.interpreter.types.GenericType
import org.fpeterek.tilscript.interpreter.types.ListType
import org.fpeterek.tilscript.interpreter.types.TupleType
import org.fpeterek.tilscript.interpreter.util.SrcPosition

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
}