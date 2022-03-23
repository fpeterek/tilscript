package org.fpeterek.til.typechecking.namechecker

import org.fpeterek.til.typechecking.constructions.*
import org.fpeterek.til.typechecking.util.SymbolRepository

class NameChecker private constructor(
    private val symbolRepository: SymbolRepository,
    private val parent: NameChecker?) {

    companion object {
        fun checkSymbols(construction: Construction, repository: SymbolRepository) =
            NameChecker(symbolRepository = repository).check(construction)

        private fun checkSymbols(construction: Construction, parent: NameChecker) =
            NameChecker(SymbolRepository(), parent).check(construction)
    }

    constructor(symbolRepository: SymbolRepository) : this(
        SymbolRepository(),
        // We want to avoid modifying the original repo
        parent=NameChecker(symbolRepository, parent=null)
    )

    private val outermostParent: NameChecker
        get() = parent?.outermostParent ?: this

    private fun findSymbol(name: String): Boolean =
        (name in symbolRepository) || (parent?.findSymbol(name) ?: false)

    private fun processClosure(cl: Closure) {
        cl.variables.forEach {
            symbolRepository.add(it)
        }

        checkSymbols(cl.construction, this)
    }

    private fun processComposition(composition: Composition) {
        checkSymbols(composition.function, this)

        composition.args.forEach {
            checkSymbols(it, this)
        }
    }

    private fun processTrivialization(trivialization: Trivialization) =
        when (trivialization.construction) {
            is Variable, is TilFunction -> check(trivialization.construction)
            else -> checkSymbols(trivialization.construction, outermostParent)
        }

    private fun processExecution(execution: Execution) {
        check(execution.construction)
    }


    private fun processSymbol(symbol: String) {
        if (!findSymbol(symbol)) {
            throw RuntimeException("Undefined symbol '${symbol}'")
        }
    }

    private fun processVariable(variable: Variable) = processSymbol(variable.name)
    private fun processFunction(fn: TilFunction) = processSymbol(fn.name)

    fun process(construction: Construction): Unit = when (construction) {
        is Closure -> processClosure(construction)
        is Composition -> processComposition(construction)
        is Trivialization -> processTrivialization(construction)
        is Execution -> processExecution(construction)
        is Variable -> processVariable(construction)
        is TilFunction -> processFunction(construction)
        is Literal -> Unit
    }

    fun check(construction: Construction): Unit {

    }


}

