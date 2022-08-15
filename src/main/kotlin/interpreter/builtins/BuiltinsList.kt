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
        Util.Progn,
        Util.RunAll,

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
    )

    val types get() = Types.all
    val values get() = Values.all
}