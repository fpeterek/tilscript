package org.fpeterek.til.typechecking.typechecker

import org.fpeterek.til.typechecking.types.*

class TypeMatcher private constructor(val types: TypeRepository) {

    companion object {
        fun match(l: Type, r: Type, types: TypeRepository) =
            TypeMatcher(types).match(l, r)
    }

    private val generics = mutableMapOf<String, Type>()

    private fun matchTuples(l: TilTuple, r: TilTuple) = match(l.type, r.type)
    private fun matchLists(l: TilList, r: TilList) = match(l.type, r.type)

    private fun matchGenerics(l: GenericType, r: Type): Boolean {
        val exp = generics.getOrPut(l.name) { r }
        return match(r, exp)
    }

    // TODO: Continue

    private fun matchInternal(l: Type, r: Type) = when (l) {
        is AtomicType -> TODO()
        ConstructionType -> true
        is FunctionType -> TODO()
        is GenericType -> throw RuntimeException("Error: Invalid state")
        is TilList -> matchLists(l, r as TilList)
        is TilTuple -> matchTuples(l, r as TilTuple)
        is TypeAlias -> TODO()
        Unknown -> true
    }

    fun match(l: Type, r: Type): Boolean = when {
        l.javaClass != r.javaClass -> false
        l is GenericType && r is GenericType -> l.argNumber == r.argNumber
        l is GenericType -> matchGenerics(l, r)
        r is GenericType -> matchGenerics(r, l)
        else -> matchInternal(l, r)
    }

}
