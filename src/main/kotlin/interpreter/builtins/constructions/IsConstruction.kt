package org.fpeterek.tilscript.interpreter.interpreter.builtins.constructions

import org.fpeterek.tilscript.interpreter.interpreter.builtins.Types
import org.fpeterek.tilscript.interpreter.interpreter.interpreterinterface.EagerFunction
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

    object IsVariable : EagerFunction(
        "IsVariable",
        Types.Bool,
        listOf(
            Variable("name", noPos, ConstructionType)
        ),
    ) {
        override fun apply(interpreter: InterpreterInterface, args: List<Construction>, ctx: FnCallContext) =
            Bool(value = args[0] is Variable, noPos)
    }

    object IsComposition : EagerFunction(
        "IsComposition",
        Types.Bool,
        listOf(
            Variable("name", noPos, ConstructionType)
        ),
    ) {
        override fun apply(interpreter: InterpreterInterface, args: List<Construction>, ctx: FnCallContext) =
            Bool(value = args[0] is Composition, noPos)
    }

    object IsClosure : EagerFunction(
        "IsClosure",
        Types.Bool,
        listOf(
            Variable("name", noPos, ConstructionType)
        ),
    ) {
        override fun apply(interpreter: InterpreterInterface, args: List<Construction>, ctx: FnCallContext) =
            Bool(value = args[0] is Closure, noPos)
    }

    object IsExecution : EagerFunction(
        "IsExecution",
        Types.Bool,
        listOf(
            Variable("name", noPos, ConstructionType)
        ),
    ) {
        override fun apply(interpreter: InterpreterInterface, args: List<Construction>, ctx: FnCallContext) =
            Bool(value = args[0] is Execution, noPos)
    }

    object IsFunction : EagerFunction(
        "IsFunction",
        Types.Bool,
        listOf(
            Variable("name", noPos, GenericType(1))
        ),
    ) {
        override fun apply(interpreter: InterpreterInterface, args: List<Construction>, ctx: FnCallContext) =
            Bool(value = args[0] is TilFunction, noPos)
    }

    object IsTrivialization : EagerFunction(
        "IsTrivialization",
        Types.Bool,
        listOf(
            Variable("name", noPos, ConstructionType)
        ),
    ) {
        override fun apply(interpreter: InterpreterInterface, args: List<Construction>, ctx: FnCallContext) =
            Bool(value = args[0] is Trivialization, noPos)
    }

    object IsSymbol : EagerFunction(
        "IsSymbol",
        Types.Bool,
        listOf(
            Variable("name", noPos, GenericType(1))
        ),
    ) {
        override fun apply(interpreter: InterpreterInterface, args: List<Construction>, ctx: FnCallContext) =
            Bool(value = args[0] is Symbol, noPos)
    }

    object IsValue : EagerFunction(
        "IsValue",
        Types.Bool,
        listOf(
            Variable("name", noPos, GenericType(1))
        ),
    ) {
        override fun apply(interpreter: InterpreterInterface, args: List<Construction>, ctx: FnCallContext) =
            Bool(value = args[0] is Value, noPos)
    }

    object IsList : EagerFunction(
        "IsList",
        Types.Bool,
        listOf(
            Variable("name", noPos, GenericType(1))
        ),
    ) {
        override fun apply(interpreter: InterpreterInterface, args: List<Construction>, ctx: FnCallContext) =
            Bool(value = args[0].constructedType is ListType, noPos)
    }

    object IsTuple : EagerFunction(
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