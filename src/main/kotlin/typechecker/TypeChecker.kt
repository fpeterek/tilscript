package org.fpeterek.til.typechecking.typechecker

import org.fpeterek.til.typechecking.sentence.*
import org.fpeterek.til.typechecking.tilscript.Builtins
import org.fpeterek.til.typechecking.typechecker.TypeAssignment.assignType
import org.fpeterek.til.typechecking.types.*
import org.fpeterek.til.typechecking.types.SymbolRepository
import org.fpeterek.til.typechecking.types.Util.trivialize

class TypeChecker private constructor(
    private val parent: TypeChecker?,
    private val repo: SymbolRepository = SymbolRepository(),
    private val lambdaBound: SymbolRepository = SymbolRepository(),
    private val typeRepo: TypeRepository = TypeRepository(),
) {

    companion object {
        fun process(
            construction: Construction,
            symbolRepository: SymbolRepository,
            lambdaBound: SymbolRepository,
            typeRepo: TypeRepository = TypeRepository()
        ) = TypeChecker(null, symbolRepository, lambdaBound, typeRepo).processConstruction(construction)

        private fun process(construction: Construction, parent: TypeChecker, typeRepo: TypeRepository) =
            TypeChecker(parent, lambdaBound=parent.lambdaBound, typeRepo=typeRepo).processConstruction(construction)

        fun process(
            sentences: Iterable<Sentence>,
            symbolRepository: SymbolRepository = SymbolRepository(),
            lambdaBound: SymbolRepository = SymbolRepository(),
            typeRepo: TypeRepository = TypeRepository(),
        ) = TypeChecker(null, symbolRepository, lambdaBound, typeRepo).process(sentences)
    }

    private fun fork() = TypeChecker(this)

    // Search in local repo first
    // If symbol is not found, search in parent repo
    // If symbol is not found ever still, and we have reached the outermost scope,
    // return Unknown
    private fun findSymbolType(symbol: String): Type =
        repo[symbol] ?: parent?.findSymbolType(symbol) ?: Unknown

    private fun lambdaBoundType(symbol: String) = lambdaBound[symbol] ?: Unknown

    private val outermostRepo: SymbolRepository
        get() = parent?.outermostRepo ?: repo

    private fun match(l: Type, r: Type) = TypeMatcher.match(l, r, typeRepo)

    private fun processTrivialization(trivialization: Trivialization): Trivialization {

        val processed = when (trivialization.construction) {
            is TilFunction, is Literal -> process(trivialization.construction, this, typeRepo)

            // Binding by trivialization -> variables from outer scopes
            // are inaccessible
            else -> process(trivialization.construction, outermostRepo, lambdaBound, typeRepo)
        }.trivialize()

        // We use constructedType to store the type of literals and functions
        // even though literals (i.e. values from base) and functions themselves do not
        // construct anything
        // To clarify, functions must be applied onto (zero or more) arguments using compositions,
        // to construct anything
        val type = when (processed.construction) {
            is Literal, is TilFunction -> processed.construction.constructedType

            // Otherwise, trivialization constructs a construction
            else -> ConstructionType
        }

        return processed.assignType(typeRepo.process(type))
    }

    private fun processVariable(variable: Variable) =
        variable.assignType(findSymbolType(variable.name))

    private fun processExecution(execution: Execution) = with(execution) {

        if (construction !is Composition) {
            throw RuntimeException("Only compositions can be executed")
        }

        val processedConstruction = processConstruction(construction)
        val firstExecution = Execution(processedConstruction, executionOrder, execution.position)

        if (executionOrder == 1) {
            firstExecution
        } else {
            firstExecution.construction as Composition
            if (firstExecution.construction.constructedType !is ConstructionType) {
                throw RuntimeException("Objects from object base cannot be executed")
            }
            // Unknown type as we may not be able to determine the type constructed
            // by the constructed construction
            // Yes, things start to get somewhat convoluted at this point
            Execution(
                processedConstruction,
                2,
                execution.position
            )
        }
    }

    private fun execute(construction: Construction) = when (construction) {
        is Literal -> throw RuntimeException("Literals cannot be executed.")
        is TilFunction -> throw RuntimeException("Functions cannot be executed.")
        else -> processConstruction(construction)
    }

    private fun processCompositionFn(fn: Construction) = processConstruction(fn).let { processed ->
        when {
            processed is TilFunction ->
                throw RuntimeException(
                    "Functions cannot be executed, did you forget a trivialization ('${processed.name})?")
            processed.constructedType !is FunctionType -> {
                throw RuntimeException("Only functions can be applied on arguments using a composition")
            }
        }

        processed
    }

    private fun processOperatorArgs(args: List<Construction>) = when {
        args.isEmpty() -> throw RuntimeException("No arguments supplied")

        else -> {
            val isInt = match(execute(args.first()).constructedType, Builtins.Nu)
            val expType = when {
                isInt -> Builtins.Nu
                else -> Builtins.Eta
            }

            processCompositionArgs(args, listOf(expType, expType))
        }
    }


    private fun processCompositionArgs(args: List<Construction>, expected: List<Type>) = when {
        args.size > expected.size -> throw RuntimeException("Too many arguments (expected ${expected.size}, received ${args.size})")
        args.size < expected.size -> throw RuntimeException("Too few arguments (expected ${expected.size}, received ${args.size})")

       else -> args.zip(expected).map { (cons, expType) ->
                val processed = execute(cons)

                if (expType !is Unknown && !match(expType, processed.constructedType)) {
                    throw RuntimeException(
                        "Function argument type mismatch. " +
                                "Expected '${expType}', Got '${processed.constructedType}'"
                    )
                }

                processed
            }
    }

    private val numericOperator = setOf("+", "-", "*", "/")

    private fun processCompositionWithNumOp(composition: Composition, op: TilFunction) = with(composition) {

        val processedArgs = processOperatorArgs(args)

        val isInt = match(processedArgs.first().constructedType, Builtins.Nu)
        val opType = when {
            isInt -> Builtins.Nu
            else -> Builtins.Eta
        }

        val fn = TilFunction(op.name, op.position, FunctionType(opType, opType, opType))

        Composition(fn, processedArgs, position, opType)
    }

    private fun processCompositionWithNumOp(composition: Composition, fn: Construction) =
        processCompositionWithNumOp(composition, (fn as Trivialization).construction as TilFunction)

    private fun processCompositionWithFn(composition: Composition, fn: Construction) = with(composition) {
        val fnType = typeRepo.process(fn.constructedType as FunctionType)
        val fnArgs = fnType.argTypes

        val processedArgs = processCompositionArgs(args, fnArgs)

        Composition(fn, processedArgs, position, fnType.imageType)
    }

    private fun processComposition(composition: Composition) = with(composition) {

        val fn = processCompositionFn(function)

        val isNumericOp = fn is Trivialization && fn.construction is TilFunction &&
                fn.construction.name in numericOperator

        when (isNumericOp) {
            true  -> processCompositionWithNumOp(composition, fn)
            false -> processCompositionWithFn(composition, fn)
        }
    }

    private fun processClosureForked(closure: Closure) = with(closure) {

        val vars = variables.map {

            val typed = when (it.constructedType) {
                !is Unknown -> it
                else -> it.assignType(typeRepo.process(lambdaBoundType(it.name)))
            }

            if (typed.constructedType is Unknown) {
                throw RuntimeException("Undefined lambda bound variable '${typed.name}'")
            }
            repo.add(typed)

            typed
        }

        val abstracted = process(construction, this@TypeChecker, typeRepo)

        Closure(vars, abstracted, position).assignType()
    }

    private fun processClosure(closure: Closure) = fork().processClosureForked(closure)

    private fun assignFnType(function: TilFunction) = findSymbolType(function.name).let { type ->

        if (type !is FunctionType) {
            throw RuntimeException("${function.name} is not a function")
        }

        function.assignType(type)
    }

    private fun processFunction(fn: TilFunction) = when (fn.constructedType) {
        !is Unknown -> fn
        else -> assignFnType(fn)
    }

    private fun processLiteral(literal: Literal) = when (literal.constructedType) {
        !is Unknown -> literal
        else -> literal.assignType(findSymbolType(literal.value))
    }

    private fun processConstruction(construction: Construction): Construction = when (construction) {
        is Closure -> processClosure(construction)
        is Composition -> processComposition(construction)
        is Trivialization -> processTrivialization(construction)
        is Execution -> processExecution(construction)
        is Variable -> processVariable(construction)
        is TilFunction -> processFunction(construction)
        is Literal -> processLiteral(construction)
    }

    private fun addLiteral(lit: Literal) = Literal(
        lit.value,
        lit.position,
        typeRepo.process(lit.constructedType),
    ).apply {
        repo.add(this)
    }

    private fun processSingleDef(lit: Literal) = when (lit.value) {
        in repo -> throw RuntimeException("Redefinition of symbol '${lit.value}'")
        else -> addLiteral(lit)
    }

    private fun processLiteralDefinition(def: LiteralDefinition) = def.apply {
        def.literals.forEach(::processSingleDef)
    }

    private fun processTypeDefinition(def: TypeDefinition) = def.apply {
        when (alias.name) {
            in typeRepo -> throw RuntimeException("Redefinition of type '${def.alias.name}'")
            else -> typeRepo.process(def.alias)
        }
    }

    private fun processSingleDef(variable: Variable) = when (variable.name) {
        in repo -> throw RuntimeException("Redefinition of symbol '${variable.name}'")
        else -> repo.add(variable)
    }

    private fun processVariableDefinition(def: VariableDefinition) = def.apply {
        def.variables.forEach(::processSingleDef)
    }

    private fun processSingleDef(fn: TilFunction) = when (fn.name) {
        in repo -> throw RuntimeException("Redefinition of symbol '${fn.name}'")
        else -> repo.add(fn)
    }

    private fun processFunctionDefinition(def: FunctionDefinition) = def.apply {
        def.functions.forEach(::processSingleDef)
    }

    private fun processDefinition(definition: Definition): Definition = when (definition) {
        is LiteralDefinition -> processLiteralDefinition(definition)
        is TypeDefinition -> processTypeDefinition(definition)
        is VariableDefinition -> processVariableDefinition(definition)
        is FunctionDefinition -> processFunctionDefinition(definition)
    }

    private fun process(sentence: Sentence): Sentence = when (sentence) {
        is Definition -> processDefinition(sentence)
        is Construction -> processConstruction(sentence)
    }

    fun process(sentences: Iterable<Sentence>): List<Sentence> = sentences.map(::process)

}
