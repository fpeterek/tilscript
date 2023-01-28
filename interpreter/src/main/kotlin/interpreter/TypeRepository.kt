package org.fpeterek.tilscript.interpreter.interpreter

import org.fpeterek.tilscript.common.types.*


class TypeRepository {

    private val types = mutableMapOf<String, Type>()

    private fun addType(type: AtomicType) {
        types[type.name] = type
    }

    private fun addAlias(alias: TypeAlias) {
        types[alias.name] = alias
    }

    private fun addType(type: StructType) {
        types[type.name] = type
    }

    fun process(type: AtomicType) = when (type.name) {
        !in types -> type.apply(::addType)
        else -> type
    }

    fun process(alias: TypeAlias) = when (alias.name) {
        !in types -> alias.apply(::addAlias)
        else -> alias
    }

    fun process(functionType: FunctionType): FunctionType = FunctionType(
        process(functionType.imageType),
        functionType.argTypes.map(::process)
    )

    fun process(type: StructType) = when (type.name) {
        !in types -> type.apply(::addType)
        else -> type
    }

    fun process(type: Type) = when (type) {
        is TypeAlias  -> process(type)
        is AtomicType -> process(type)
        is StructType -> process(type)
        else -> type
    }

    operator fun get(name: String) = types[name]
    operator fun contains(name: String) = name in types

    fun isFunction(name: String): Boolean = when (types[name]) {
        null -> false
        is FunctionType -> true
        is TypeAlias -> isFunction((types[name]!! as TypeAlias).name)
        else -> false
    }

    fun isAtomic(name: String): Boolean = when (types[name]) {
        null -> false
        is AtomicType -> true
        is TypeAlias -> isAtomic((types[name]!! as TypeAlias).name)
        else -> false
    }

}
