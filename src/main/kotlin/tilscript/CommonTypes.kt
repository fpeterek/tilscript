package org.fpeterek.til.typechecking.tilscript

import org.fpeterek.til.typechecking.interpreter.builtins.FnDeclarations
import org.fpeterek.til.typechecking.interpreter.builtins.Types
import org.fpeterek.til.typechecking.types.FunctionType
import org.fpeterek.til.typechecking.types.Type
import org.fpeterek.til.typechecking.types.Util.intensionalize

object CommonTypes {
    val office get() = Types.Indiv.intensionalize()
    val proposition get() = Types.Bool.intensionalize()
    val extensionProperty get() = FunctionType(Types.Bool, Types.Indiv)
    val property get() = extensionProperty.intensionalize()
    val setOfSets get() = FunctionType(
        FunctionType(Types.Bool, extensionProperty),
        extensionProperty
    )
    val binaryOmicron get() = FunctionType(Types.Bool, Types.Bool, Types.Bool)

    fun setOfSets(alpha: Type) = FunctionType(
        FunctionType(Types.Bool, FunctionType(Types.Bool, alpha)),
        FunctionType(Types.Bool, alpha)
    )
}