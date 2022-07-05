package org.fpeterek.til.typechecking.tilscript

import org.fpeterek.til.typechecking.types.FunctionType
import org.fpeterek.til.typechecking.types.Type
import org.fpeterek.til.typechecking.types.Util.intensionalize

object CommonTypes {
    val office get() = Builtins.Indiv.intensionalize()
    val proposition get() = Builtins.Bool.intensionalize()
    val extensionProperty get() = FunctionType(Builtins.Bool, Builtins.Indiv)
    val property get() = extensionProperty.intensionalize()
    val setOfSets get() = FunctionType(
        FunctionType(Builtins.Bool, extensionProperty),
        extensionProperty
    )
    val binaryOmicron get() = FunctionType(Builtins.Bool, Builtins.Bool, Builtins.Bool)

    fun setOfSets(alpha: Type) = FunctionType(
        FunctionType(Builtins.Bool, FunctionType(Builtins.Bool, alpha)),
        FunctionType(Builtins.Bool, alpha)
    )
}