package org.fpeterek.til.typechecking.tilscript

import org.fpeterek.til.typechecking.types.FunctionType
import org.fpeterek.til.typechecking.types.Type
import org.fpeterek.til.typechecking.types.Util.intensionalize

object CommonTypes {
    val office get() = Builtins.Iota.intensionalize()
    val proposition get() = Builtins.Omicron.intensionalize()
    val extensionProperty get() = FunctionType(Builtins.Omicron, Builtins.Iota)
    val property get() = extensionProperty.intensionalize()
    val setOfSets get() = FunctionType(
        FunctionType(Builtins.Omicron, extensionProperty),
        extensionProperty
    )
    val binaryOmicron get() = FunctionType(Builtins.Omicron, Builtins.Omicron, Builtins.Omicron)

    fun setOfSets(alpha: Type) = FunctionType(
        FunctionType(Builtins.Omicron, FunctionType(Builtins.Omicron, alpha)),
        FunctionType(Builtins.Omicron, alpha)
    )
}