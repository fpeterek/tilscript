package org.fpeterek.til.typechecking.typechecker

import org.fpeterek.til.typechecking.constructions.*
import org.fpeterek.til.typechecking.typechecker.TypeAssignment.assignType
import org.fpeterek.til.typechecking.types.ConstructionType
import org.fpeterek.til.typechecking.types.Unknown
import org.fpeterek.til.typechecking.util.Util
import org.fpeterek.til.typechecking.util.Util.incrementOrder
import org.fpeterek.til.typechecking.util.Util.trivialize

class TypeChecker private constructor(
    val parent: TypeChecker?,
    val repo: SymbolRepository = SymbolRepository()
){

    companion object {
        fun process(construction: Construction, symbolRepository: SymbolRepository) =
            TypeChecker(null, symbolRepository).process(construction)

        private fun process(construction: Construction, parent: TypeChecker) =
            TypeChecker(parent).process(construction)
    }

    private fun processTrivialization(trivialization: Trivialization) =
        process(trivialization.construction, this).trivialize().assignType()

    private fun processExecution(execution: Execution) = with(execution) {

        if (construction !is Composition) {
            throw RuntimeException("Only compositions can be executed")
        }

        // TODO: Double execution

        Execution(process(construction), executionOrder).assignType()
    }

    // TODO: Implement
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
            is Composition -> processComposition(construction)
            is Trivialization -> processTrivialization(construction)
            is Execution -> processExecution(construction)
            else -> Util.w
        }



        // TODO: Return something useful
        return Util.w
    }

}
