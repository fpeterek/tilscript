package org.fpeterek.til.typechecking.typechecker

import org.fpeterek.til.typechecking.constructions.TilFunction
import org.fpeterek.til.typechecking.constructions.Variable
import org.fpeterek.til.typechecking.types.Type
import org.fpeterek.til.typechecking.types.Unknown

class SymbolRepository {
    private val types = mutableMapOf<String, Type>()

    fun add(name: String, type: Type) {
        when (types[name]) {
            null, is Unknown -> types[name] = type
            else -> throw RuntimeException("Symbol '$name' is already defined")
        }
    }

    fun add(variable: Variable) = add(variable.name, variable.type)
    fun add(function: TilFunction) = add(function.name, function.type)

    operator fun get(name: String) = types[name]

    fun isDefined(name: String) = name in types

    fun isKnown(name: String) = when (types[name]) {
        null, is Unknown -> false
        else -> false
    }
}
