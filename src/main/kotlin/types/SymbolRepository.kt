package org.fpeterek.tilscript.interpreter.types

import org.fpeterek.tilscript.interpreter.sentence.Construction
import org.fpeterek.tilscript.interpreter.sentence.Value
import org.fpeterek.tilscript.interpreter.sentence.TilFunction
import org.fpeterek.tilscript.interpreter.sentence.Variable


class SymbolRepository {

    companion object {
        fun withBuiltins() = SymbolRepository()
    }

    private val types = mutableMapOf<String, Type>()

    private val declared = mutableSetOf<String>()
    private val defined = mutableSetOf<String>()

    val symbols get() = types.asSequence().map { it.key }

    fun declare(name: String, type: Type) {
        declared.add(name)
        types[name] = type
    }

    fun declare(variable: Variable) = declare(variable.name, variable.constructedType)
    fun declare(function: TilFunction) = declare(function.name, function.constructedType)
    fun declare(literal: Value) = declare(literal.toString(), literal.constructedType)

    fun declare(construction: Construction) = when (construction) {
        is Variable    -> declare(construction)
        is TilFunction -> declare(construction)
        is Value       -> declare(construction)
        else           -> throw RuntimeException("Only variable, function and literal types can be stored in repos")
    }

    fun declareAll(constructions: Iterable<Construction>) = constructions.forEach(::declare)

    fun define(name: String, type: Type) {
        if (name in defined) {
            throw RuntimeException("Redefinition of symbol '$name'")
        }
        defined.add(name)
        declare(name, type)
    }

    fun define(variable: Variable) = define(variable.name, variable.constructedType)
    fun define(function: TilFunction) = define(function.name, function.constructedType)
    fun define(literal: Value) = define(literal.toString(), literal.constructedType)

    fun define(construction: Construction) = when (construction) {
        is Variable    -> define(construction)
        is TilFunction -> define(construction)
        is Value       -> define(construction)
        else           -> throw RuntimeException("Only variable, function and literal types can be stored in repos")
    }

    fun defineAll(constructions: Iterable<Construction>) = constructions.forEach(::define)

    operator fun contains(name: String) = name in types

    operator fun get(name: String) = types[name]

    fun isDefined(name: String) = name in defined
    fun isDeclared(name: String) = name in declared

    fun isKnown(name: String) = when (types[name]) {
        null, is Unknown -> false
        else -> false
    }
}
