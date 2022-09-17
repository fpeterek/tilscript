package org.fpeterek.tilscript.stdlib

import org.fpeterek.tilscript.common.interpreterinterface.SymbolRegistrar
import org.fpeterek.tilscript.common.sentence.Symbol
import org.fpeterek.tilscript.common.sentence.TilFunction
import org.fpeterek.tilscript.common.types.TypeAlias
import org.fpeterek.tilscript.stdlib.constructions.*


object StdlibRegistrar : SymbolRegistrar {

    override val functions get() = listOf(
        ListFunctions.EmptyListOf,
        ListFunctions.Cons,
        ListFunctions.Head,
        ListFunctions.Tail,
        ListFunctions.IsEmpty,
        ListFunctions.ListOfOne,
        ListFunctions.EmptyList,

        Util.Print,
        Util.Println,
        Util.If,
        Util.Progn,
        Util.Tr,
        Util.TypeOf,
        Util.GetWorld,
        Util.IsNil,

        LogicFunctions.Not,
        LogicFunctions.And,
        LogicFunctions.Or,
        LogicFunctions.Implies,

        TupleFunctions.MkTuple,
        TupleFunctions.Get,

        TextFunctions.HeadS,
        TextFunctions.TailS,
        TextFunctions.CatS,
        TextFunctions.Char,
        TextFunctions.LenS,

        TimeFunctions.IsBefore,
        TimeFunctions.IsBeforeOrEq,
        TimeFunctions.IsAfter,
        TimeFunctions.IsAfterOrEq,
        TimeFunctions.Now,

        Conversions.IntToText,
        Conversions.RealToText,
        Conversions.ToTime,
        Conversions.TextToInt,
        Conversions.TextToReal,
        Conversions.TimeToInt,
        Conversions.ToReal,
        Conversions.ToInt,

        ClosureFunctions.ConsClosure,
        ClosureFunctions.ClosureBody,
        ClosureFunctions.ClosureArgs,
        ClosureFunctions.ClosureReturnType,

        IsConstruction.IsVariable,
        IsConstruction.IsComposition,
        IsConstruction.IsClosure,
        IsConstruction.IsExecution,
        IsConstruction.IsFunction,
        IsConstruction.IsTrivialization,
        IsConstruction.IsSymbol,
        IsConstruction.IsList,
        IsConstruction.IsValue,
        IsConstruction.IsTuple,

        VariableFunctions.ConsVariable,
        VariableFunctions.GetVariable,
        VariableFunctions.VariableType,
        VariableFunctions.VariableName,

        TrivializationFunctions.Trivialize,
        TrivializationFunctions.TrivializationBody,

        ExecutionFunctions.Execute,
        ExecutionFunctions.ExecutionBody,
        ExecutionFunctions.ExecutionOrder,

        FunctionFunctions.CreateFunctionRef,
        FunctionFunctions.GetFunction,
        FunctionFunctions.FunctionName,
        FunctionFunctions.FunctionType,

        CompositionFunctions.Compose,
        CompositionFunctions.CompositionConstructions,
        CompositionFunctions.CompositionConstructionAt,

        TypeFunctions.GetType,
        TypeFunctions.ConsFunctionType,
        TypeFunctions.FunctionTypeAsList,
        TypeFunctions.FunctionTypeAt,
        TypeFunctions.TupleTypeAt,
        TypeFunctions.ConsTupleType,
        TypeFunctions.ConsListType,
        TypeFunctions.ListValueType,
        TypeFunctions.ConsGenericType,
        TypeFunctions.GenericTypeNumber,
    )

    val types get()  = Types.all
    val values get() = Values.all

    override val aliases = emptyList<TypeAlias>()
    override val symbols = emptyList<Symbol>()
    override val functionDeclarations get() = FunctionDeclarations.builtinFunctions
}