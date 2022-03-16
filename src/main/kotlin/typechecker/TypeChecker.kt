package org.fpeterek.til.typechecking.typechecker

import org.fpeterek.til.typechecking.constructions.*
import org.fpeterek.til.typechecking.typechecker.TypeAssignment.assignType
import org.fpeterek.til.typechecking.types.ConstructionType
import org.fpeterek.til.typechecking.types.FunctionType
import org.fpeterek.til.typechecking.types.Type
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

    // TODO: Handle trivialization bound variables properly

    // Search in local repo first
    // If symbol is not found, search in parent repo
    // If symbol is not found ever still, and we have reached the outmost scope,
    // return Unknown and hope the type can be inferred
    private fun findSymbolType(symbol: String): Type =
        repo[symbol] ?: parent?.findSymbolType(symbol) ?: Unknown

    // TODO: Handle trivialization bound variables properly
    private fun processTrivialization(trivialization: Trivialization) =
        process(trivialization.construction, this).trivialize().assignType()

    private fun processVariable(variable: Variable) =
        variable.assignType(findSymbolType(variable.name))

    private fun processExecution(execution: Execution) = with(execution) {

        if (construction !is Composition) {
            throw RuntimeException("Only compositions can be executed")
        }

        // TODO: Double execution

        val firstExecution = Execution(process(construction), executionOrder).assignType()

        if (executionOrder == 1) {
            firstExecution
        } else {
            throw UnsupportedOperationException("Double execution is not supported yet")
            firstExecution.construction as Composition
            if (firstExecution.construction.constructsType !is ConstructionType) {
                throw RuntimeException("")
            }
        }

        firstExecution
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

    private fun processFunction(function: TilFunction) = findSymbolType(function.name).let { type ->

        if (type !is FunctionType) {
            throw RuntimeException("${function.name} is not a function")
        }

        function.assignType(type)
    }

    fun process(construction: Construction): Construction = when (construction) {
        is Closure -> processClosure(construction)
        is Composition -> processComposition(construction)
        is Trivialization -> processTrivialization(construction)
        is Execution -> processExecution(construction)
        is Variable -> processVariable(construction)
        is TilFunction -> processFunction(construction)
    }

}
