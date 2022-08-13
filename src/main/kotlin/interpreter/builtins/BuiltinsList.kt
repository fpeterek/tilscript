package org.fpeterek.tilscript.interpreter.interpreter.builtins


object BuiltinsList {
    val functions = listOf(
        ListFunctions.EmptyListOf,
        ListFunctions.Cons,
        ListFunctions.Head,
        ListFunctions.Tail,
        ListFunctions.IsEmpty,

        Util.Print,
        Util.Println,
        Util.If,
        Util.Chain,
        Util.RunAll,

        LogicFunctions.Not,
        LogicFunctions.And,
        LogicFunctions.Or,
        LogicFunctions.Implies,

        TupleFunctions.MkTuple,
        TupleFunctions.Get
    )

    val types get() = Types.all
    val values get() = Values.all
}