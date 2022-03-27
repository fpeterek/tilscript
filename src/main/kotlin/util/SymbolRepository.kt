package org.fpeterek.til.typechecking.util

import org.fpeterek.til.typechecking.constructions.Construction
import org.fpeterek.til.typechecking.constructions.Literal
import org.fpeterek.til.typechecking.constructions.TilFunction
import org.fpeterek.til.typechecking.constructions.Variable
import org.fpeterek.til.typechecking.types.Type
import org.fpeterek.til.typechecking.types.Unknown

class SymbolRepository(vararg symbols: Construction) {

    private val types = mutableMapOf<String, Type>()

    init {
        symbols.forEach(::add)
    }

    fun add(name: String, type: Type) {
        val current = types[name]

        when {
            current == null || current is Unknown -> types[name] = type
            current != type -> throw RuntimeException("Symbol '$name' is already with a different type")
            else -> Unit
        }
    }

    fun add(variable: Variable) = add(variable.name, variable.constructedType)
    fun add(function: TilFunction) = add(function.name, function.constructedType)
    fun add(literal: Literal) = add(literal.value, literal.constructedType)

    fun add(construction: Construction) = when (construction) {
        is Variable -> add(construction)
        is TilFunction -> add(construction)
        is Literal -> add(construction)
        else -> throw RuntimeException("Only variable, function and literal types can be stored in repos")
    }

    operator fun contains(name: String) = name in types

    operator fun get(name: String) = types[name]

    fun isDefined(name: String) = contains(name)

    fun isKnown(name: String) = when (types[name]) {
        null, is Unknown -> false
        else -> false
    }
}
