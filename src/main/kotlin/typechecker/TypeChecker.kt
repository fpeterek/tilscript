package org.fpeterek.til.typechecking.typechecker

import org.fpeterek.til.typechecking.types.AtomicType
import org.fpeterek.til.typechecking.types.ConstructionType
import org.fpeterek.til.typechecking.types.FunctionType
import org.fpeterek.til.typechecking.types.Type

class TypeChecker {

    private fun atomicTypeCheck(type1: AtomicType, type2: Type) = when (type2) {
        is AtomicType -> type1 == type2
        else -> false
    }

    private fun constructionTypeCheck(type1: ConstructionType, type2: Type) = when (type2) {
        is ConstructionType -> type1.order == type2.order
        else -> false
    }

    private fun fnArityCheck(fn1: FunctionType, fn2: FunctionType) = fn1.arity == fn2.arity

    private fun fnImageCheck(fn1: FunctionType, fn2: FunctionType) =
        typesMatch(fn1.imageType, fn2.imageType)

    private fun fnArgsCheck(fn1: FunctionType, fn2: FunctionType) = fn1.argTypes.asSequence()
        .zip(fn2.argTypes.asSequence())
        .all { (t1, t2) -> typesMatch(t1, t2) }

    private fun twoFunctionsTypeCheck(fn1: FunctionType, fn2: FunctionType) =
        fnArityCheck(fn1, fn2) && fnImageCheck(fn1, fn2) && fnArgsCheck(fn1, fn2)

    private fun functionTypeCheck(type1: FunctionType, type2: Type) = when (type2) {
        is FunctionType -> twoFunctionsTypeCheck(type1, type2)
        else -> false
    }

    fun typesMatch(type1: Type, type2: Type): Boolean = when (type1) {
        is AtomicType -> atomicTypeCheck(type1, type2)
        is ConstructionType -> constructionTypeCheck(type1, type2)
        is FunctionType -> functionTypeCheck(type1, type2)
    }

}
