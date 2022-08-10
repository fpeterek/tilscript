package org.fpeterek.til.interpreter.namechecker

import org.fpeterek.til.interpreter.reporting.Report
import org.fpeterek.til.interpreter.sentence.*
import org.fpeterek.til.interpreter.types.SymbolRepository
import org.fpeterek.til.interpreter.types.Unknown

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

        private fun checkSymbols(defn: FunctionDefinition, parent: NameChecker) =
            NameChecker(SymbolRepository(), parent=parent).processDefnScoped(defn)
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
            symbolRepository.define(it)
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

    private fun processLiteral(literal: Value) = when {
        literal !is Symbol        -> literal
        findSymbol(literal.value) -> literal
        else -> literal.withReport(
            Report("Undefined symbol '${literal.value}'", literal.position)
        )
    }

    private val mathOperators = listOf("+", "-", "/", "*")

    private fun processFunction(fn: TilFunction) = when {
        fn.name in mathOperators -> fn
        findSymbol(fn.name) -> fn
        else -> fn.withReport(
            Report("Undefined symbol '${fn.name}'", fn.position)
        )
    }

    private fun processFnDecl(decl: FunctionDeclaration) = decl.apply {
        functions.forEach(symbolRepository::declare)
    }

    private fun processLitDecl(decl: LiteralDeclaration) = decl.apply {
        literals.forEach(symbolRepository::declare)
    }

    private fun processTypeDef(def: TypeDefinition) = def

    private fun processVarDecl(decl: VariableDeclaration) = decl.apply {
        variables.forEach(symbolRepository::declare)
    }

    private fun processDefnScoped(def: FunctionDefinition): FunctionDefinition {

        val withRedef = when (def.name) {
            in symbolRepository -> def.withReport(Report("Redefinition of symbol ${def.name}", def.position))
            else -> def
        }

        symbolRepository.define(withRedef.tilFunction)

        withRedef.args.forEach(symbolRepository::define)

        return FunctionDefinition(
            withRedef.name,
            withRedef.args,
            withRedef.constructsType,
            process(withRedef.construction),
            withRedef.position,
            withRedef.reports,
        )
    }

    private fun processFunctionDef(def: FunctionDefinition) = checkSymbols(def, this)

    private fun processVariableDef(def: VariableDefinition) = when (def.name) {
        in symbolRepository -> def.withReport(Report("Redefinition of symbol '${def.name}'", def.position))
        else -> def
    }.apply {
        symbolRepository.define(variable)
    }

    private fun process(construction: Construction): Construction = when (construction) {
        is Closure        -> processClosure(construction)
        is Composition    -> processComposition(construction)
        is Trivialization -> processTrivialization(construction)
        is Execution      -> processExecution(construction)
        is Variable       -> processVariable(construction)
        is TilFunction    -> processFunction(construction)
        is Value          -> processLiteral(construction)
    }

    private fun process(declaration: Declaration): Declaration = when (declaration) {
        is FunctionDeclaration -> processFnDecl(declaration)
        is LiteralDeclaration  -> processLitDecl(declaration)
        is TypeDefinition      -> processTypeDef(declaration)
        is VariableDeclaration -> processVarDecl(declaration)
        is FunctionDefinition  -> processFunctionDef(declaration)
        is VariableDefinition  -> processVariableDef(declaration)
    }

    private fun process(sentence: Sentence): Sentence = when (sentence) {
        is Construction -> process(sentence)
        is Declaration  -> process(sentence)
    }

    private fun process(sentences: Iterable<Sentence>): List<Sentence> = sentences.map(::process)

    private fun processNewScope(construction: Construction): Construction = checkSymbols(construction, this)
}

