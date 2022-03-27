package org.fpeterek.til.typechecking.namechecker

import org.fpeterek.til.typechecking.constructions.*
import org.fpeterek.til.typechecking.util.SymbolRepository

class NameChecker private constructor(
    private val symbolRepository: SymbolRepository,
    private val parent: NameChecker?) {

    companion object {
        fun checkSymbols(construction: Construction, repository: SymbolRepository) =
            NameChecker(symbolRepository = repository).process(construction)

        private fun checkSymbols(construction: Construction, parent: NameChecker) =
            NameChecker(SymbolRepository(), parent).process(construction)
    }

    constructor(symbolRepository: SymbolRepository) : this(
        SymbolRepository(),
        // We want to avoid modifying the original repo,
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

        processNewScope(cl.construction)
    }

    private fun processComposition(composition: Composition) {
        process(composition.function)

        composition.args.forEach {
            process(it)
        }
    }

    private fun processTrivialization(trivialization: Trivialization): Unit =
        when (trivialization.construction) {
            is Variable, is TilFunction -> process(trivialization.construction)
            else -> checkSymbols(trivialization.construction, outermostParent)
        }

    private fun processExecution(execution: Execution): Unit = process(execution.construction)

    private fun processSymbol(symbol: String) {
        if (!findSymbol(symbol)) {
            throw RuntimeException("Undefined symbol '${symbol}'")
        }
    }

    private fun processVariable(variable: Variable): Unit = processSymbol(variable.name)
    private fun processFunction(fn: TilFunction): Unit = processSymbol(fn.name)

    private fun process(construction: Construction): Unit = when (construction) {
        is Closure -> processClosure(construction)
        is Composition -> processComposition(construction)
        is Trivialization -> processTrivialization(construction)
        is Execution -> processExecution(construction)
        is Variable -> processVariable(construction)
        is TilFunction -> processFunction(construction)
        is Literal -> Unit
    }

    private fun processNewScope(construction: Construction): Unit = checkSymbols(construction, this)

}

