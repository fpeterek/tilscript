package org.fpeterek.til.typechecking.typechecker

import org.fpeterek.til.typechecking.constructions.Closure
import org.fpeterek.til.typechecking.constructions.Composition
import org.fpeterek.til.typechecking.constructions.Construction
import org.fpeterek.til.typechecking.typechecker.TypeAssignment.assignType
import org.fpeterek.til.typechecking.types.Unknown
import org.fpeterek.til.typechecking.util.Util

class TypeChecker private constructor(
    val parent: TypeChecker?,
    val repo: SymbolRepository = SymbolRepository()
){

    companion object TypeChecker {
        fun process(construction: Construction, symbolRepository: SymbolRepository) =
            TypeChecker(null, symbolRepository).process(construction)
    }

    private fun processComposition(composition: Composition) = composition

    private fun processClosure(closure: Closure) = with(closure) {
        variables.forEach {
            repo.add(it)
        }

        val composition = when (construction) {
            is Composition -> processComposition(construction)
            else -> throw RuntimeException("A closure must be an abstraction over a composition")
        }

        val vars = variables.map {
            it.assignType(repo[it.name] ?: Unknown)
        }

        assignType(vars, composition)
    }

    fun process(construction: Construction): Construction {

        when (construction) {
            is Closure -> processClosure(construction)
            else -> Util.w
        }



        // TODO: Return something useful
        return Util.w
    }

}
