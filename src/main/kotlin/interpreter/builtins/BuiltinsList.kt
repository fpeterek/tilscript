package org.fpeterek.tilscript.interpreter.interpreter.builtins

import org.fpeterek.tilscript.interpreter.interpreter.builtins.constructions.*
import java.time.Clock


object BuiltinsList {
    val functions = listOf(
        ListFunctions.EmptyListOf,
        ListFunctions.Cons,
        ListFunctions.Head,
        ListFunctions.Tail,
        ListFunctions.IsEmpty,
        ListFunctions.ListOfOne,

        Util.Print,
        Util.Println,
        Util.If,
        Util.Progn,
        Util.Tr,
        Util.TypeOf,
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
        Conversions.ToWorld,
        Conversions.TextToInt,
        Conversions.TextToReal,
        Conversions.WorldToInt,
        Conversions.TimeToInt,

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
    )

    val types get() = Types.all
    val values get() = Values.all
}