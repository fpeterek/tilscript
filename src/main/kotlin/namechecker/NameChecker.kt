package org.fpeterek.til.typechecking.namechecker

import org.fpeterek.til.typechecking.reporting.Report
import org.fpeterek.til.typechecking.sentence.*
import org.fpeterek.til.typechecking.types.SymbolRepository
import org.fpeterek.til.typechecking.types.Unknown

class NameChecker private constructor(
    private val symbolRepository: SymbolRepository,
    private val parent: NameChecker?) {

    companion object {
        fun checkSymbols(sentences: Iterable<Sentence>, repository: SymbolRepository) =
            NameChecker(repository).process(sentences)

        fun checkSymbols(construction: Construction, repository: SymbolRepository) =
            NameChecker(symbolRepository = repository).process(construction)

        private fun checkSymbols(construction: Construction, parent: NameChecker) =
            NameChecker(SymbolRepository(), parent).process(construction)

        private fun checkSymbols(closure: Closure, parent: NameChecker) =
            NameChecker(SymbolRepository(), parent=parent).processClosureScoped(closure)
    }

    constructor(symbolRepository: SymbolRepository) : this(
        SymbolRepository(),
        // We want to avoid modifying the original repo,
        parent=NameChecker(symbolRepository, parent=null)
    )

    // Global scope -- above script level
    private val outermostParent: NameChecker
        get() = parent?.outermostParent ?: this

    // Script-level scope
    private val outermostModifiableParent: NameChecker
        get() = when {
            parent == null -> this
            parent.parent == null -> this
            else -> parent.outermostModifiableParent
        }

    private fun findSymbol(name: String): Boolean =
        (name in symbolRepository) || (parent?.findSymbol(name) ?: false)

    private fun processClosureScoped(cl: Closure): Closure {
        cl.variables.forEach {
            symbolRepository.add(it)
        }

        return Closure(
            cl.variables,
            processNewScope(cl.construction),
            cl.position,
            Unknown,
            cl.reports,
        )
    }

    private fun processClosure(cl: Closure) = checkSymbols(cl, this)

    private fun processComposition(composition: Composition) = Composition(
        process(composition.function),
        composition.args.map { process(it) },
        composition.position,
        Unknown,
        composition.reports,
    )

    private fun processTrivialization(trivialization: Trivialization) = Trivialization(
        when (trivialization.construction) {
            is Variable, is TilFunction -> process(trivialization.construction)
            else -> checkSymbols(trivialization.construction, outermostModifiableParent)
        },
        trivialization.position,
        Unknown,
        Unknown,
        trivialization.reports,
    )

    private fun processExecution(execution: Execution) = Execution(
        process(execution.construction),
        execution.executionOrder,
        execution.position,
        Unknown,
        execution.reports,
    )

    private fun processVariable(variable: Variable) = when {
        findSymbol(variable.name) -> variable
        else -> variable.withReport(
            Report("Undefined symbol '${variable.name}'", variable.position)
        )
    }

    private fun processFunction(fn: TilFunction) = when {
        findSymbol(fn.name) -> fn
        else -> fn.withReport(
            Report("Undefined symbol '${fn.name}'", fn.position)
        )
    }

    private fun processFnDef(def: FunctionDefinition) = def.apply {
        functions.forEach(symbolRepository::add)
    }

    private fun processLitDef(def: LiteralDefinition) = def.apply {
        literals.forEach(symbolRepository::add)
    }

    private fun processTypeDef(def: TypeDefinition) = def

    private fun processVarDef(def: VariableDefinition) = def.apply {
        variables.forEach(symbolRepository::add)
    }

    private fun process(construction: Construction): Construction = when (construction) {
        is Closure        -> processClosure(construction)
        is Composition    -> processComposition(construction)
        is Trivialization -> processTrivialization(construction)
        is Execution      -> processExecution(construction)
        is Variable       -> processVariable(construction)
        is TilFunction    -> processFunction(construction)
        is Literal        -> construction
        else              -> throw RuntimeException("Invalid state")
    }

    private fun process(definition: Definition): Definition = when (definition) {
        is FunctionDefinition -> processFnDef(definition)
        is LiteralDefinition -> processLitDef(definition)
        is TypeDefinition -> processTypeDef(definition)
        is VariableDefinition -> processVarDef(definition)
    }

    private fun process(sentence: Sentence): Sentence = when (sentence) {
        is Construction -> process(sentence)
        is Definition -> process(sentence)
    }

    private fun process(sentences: Iterable<Sentence>): List<Sentence> = sentences.map(::process)

    private fun processNewScope(construction: Construction): Construction = checkSymbols(construction, this)
}

