package org.fpeterek.til.interpreter.interpreter

import org.fpeterek.til.interpreter.types.*

class TypeMatcher private constructor(val types: TypeRepository) {

    companion object {
        fun match(l: Type, r: Type, types: TypeRepository) =
            TypeMatcher(types).match(l, r)

        fun matchFnArgs(fn: FunctionType, args: List<Type>, types: TypeRepository) =
            TypeMatcher(types).matchFnArgs(fn, args)

        fun matchFn(fn: FunctionType, returned: Type, args: List<Type>, types: TypeRepository) =
            TypeMatcher(types).matchFn(fn, returned, args)
    }

    private val generics = mutableMapOf<String, Type>()

    private fun matchTuples(l: TilTuple, r: TilTuple) = match(l.type, r.type)
    private fun matchLists(l: ListType, r: ListType) = match(l.type, r.type)

    private fun matchGenerics(l: GenericType, r: Type) = match(r, generics.getOrPut(l.name) { r })

    private fun matchFunctions(l: FunctionType, r: FunctionType) =
        l.arity == r.arity &&
            l.signature
                .zip(r.signature)
                .all { (left, right) -> match(left, right) }

    private fun matchAtomics(l: AtomicType, r: AtomicType) = l.name == r.name

    private fun matchAlias(alias: TypeAlias, other: Type) = match(alias.type, other)

    private fun matchInternal(l: Type, r: Type) = when (l) {
        is AtomicType -> matchAtomics(l, r as AtomicType)
        is FunctionType -> matchFunctions(l, r as FunctionType)
        is ListType -> matchLists(l, r as ListType)
        is TilTuple -> matchTuples(l, r as TilTuple)

        ConstructionType, Unknown -> true

        is GenericType, is TypeAlias -> throw RuntimeException("Error: Invalid state")
    }

    fun matchFn(fn: FunctionType, returned: Type, args: List<Type>): Pair<Boolean, List<Boolean>> =
        match(fn.imageType, returned) to
                fn.argTypes.zip(args).map { (exp, rec) -> match(exp, rec) }

    fun matchFnArgs(fn: FunctionType, args: List<Type>): List<Boolean> =
        fn.argTypes.zip(args).map { (exp, rec) -> match(exp, rec) }

    fun match(l: Type, r: Type): Boolean = when {
        l is Unknown || r is Unknown -> true

        l is GenericType -> matchGenerics(l, r)
        r is GenericType -> matchGenerics(r, l)

        l is TypeAlias -> matchAlias(l, r)
        r is TypeAlias -> matchAlias(r, l)

        l.javaClass != r.javaClass -> false
        else -> matchInternal(l, r)
    }

}
