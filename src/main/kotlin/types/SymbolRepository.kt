package org.fpeterek.til.typechecking.types

import org.fpeterek.til.typechecking.sentence.Construction
import org.fpeterek.til.typechecking.sentence.Literal
import org.fpeterek.til.typechecking.sentence.TilFunction
import org.fpeterek.til.typechecking.sentence.Variable

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

    fun addAll(constructions: Iterable<Construction>) = constructions.forEach(::add)

    operator fun contains(name: String) = name in types

    operator fun get(name: String) = types[name]

    fun isDefined(name: String) = contains(name)

    fun isKnown(name: String) = when (types[name]) {
        null, is Unknown -> false
        else -> false
    }
}
