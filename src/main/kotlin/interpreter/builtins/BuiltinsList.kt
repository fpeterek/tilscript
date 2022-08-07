package org.fpeterek.til.typechecking.interpreter.builtins

object BuiltinsList {
    val all = listOf(
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
    )
}