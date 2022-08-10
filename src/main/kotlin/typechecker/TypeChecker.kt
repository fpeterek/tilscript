package org.fpeterek.til.interpreter.typechecker


import org.fpeterek.til.interpreter.reporting.Report
import org.fpeterek.til.interpreter.sentence.*
import org.fpeterek.til.interpreter.interpreter.builtins.Types
import org.fpeterek.til.interpreter.typechecker.TypeAssignment.assignType
import org.fpeterek.til.interpreter.types.*
import org.fpeterek.til.interpreter.types.Util.trivialize

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

    private val Construction.isDoubleExecution
        get() = this is Execution && executionOrder == 2

    private fun match(l: Type, r: Type) = TypeMatcher.match(l, r, typeRepo)

    private fun match(fn: FunctionType, args: List<Type>) = TypeMatcher.matchFnArgs(fn, args, typeRepo)

    private fun processTrivialization(trivialization: Trivialization): Trivialization {

        val processed = when (trivialization.construction) {
            is TilFunction -> processFunction(trivialization.construction)
            is Value -> processLiteral(trivialization.construction)
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
            is Value, is TilFunction -> processed.construction.constructedType

            // Otherwise, trivialization constructs a construction
            else -> ConstructionType
        }

        return processed.assignType(typeRepo.process(type))
    }

    private fun processVariable(variable: Variable) =
        variable.assignType(findSymbolType(variable.name))

    private fun processExecution(execution: Execution) = with(execution) {

        val processedConstruction = processConstruction(
            when (construction) {
                is Composition -> construction
                else -> construction.withReport(Report("Only compositions can be executed", construction.position))
            }
        )

        val firstExecution = Execution(
            processedConstruction, executionOrder, execution.position,
            constructedType=processedConstruction.constructedType, reports=reports
        )

        if (executionOrder == 1) {
            firstExecution
        } else {
            firstExecution.construction as Composition

            // Unknown type as we may not be able to determine the type constructed
            // by the constructed construction
            // Yes, things start to get somewhat convoluted at this point
            Execution(
                construction=processedConstruction,
                executionOrder=2,
                srcPos=execution.position,
                constructedType=Unknown,
                reports=firstExecution.reports + when(firstExecution.construction.constructedType) {
                    is ConstructionType -> listOf()
                    else -> listOf(Report("Objects from object base cannot be executed", firstExecution.construction.position))
                }
            )
        }
    }

    private fun processCompositionFn(fn: Construction) = processConstruction(fn).let { processed ->
        when {
            processed is TilFunction -> processed.withReport(
                Report("Functions cannot be executed, did you forget a trivialization?", processed.position)
            )
            processed.constructedType !is FunctionType && processed.constructedType !is Unknown -> processed.withReport(
                Report("Only functions can be applied on arguments using a composition", processed.position)
            )
            else -> processed
        }
    }

    private fun processOperatorArgs(args: List<Construction>): List<Construction> {
        val isInt = match(processConstruction(args.first()).constructedType, Types.Int)
        val expType = when {
            isInt -> Types.Int
            else -> Types.Real
        }

        return processCompositionArgs(args, listOf(expType, expType))
    }

    private fun checkArity(composition: Composition, expArity: Int) = when {
        composition.args.size == expArity -> listOf()
        composition.args.size < expArity  -> listOf(
            Report("Too few arguments (expected ${expArity}, received ${composition.args.size})", composition.position)
        )
        else -> listOf(
            Report("Too few arguments (expected ${expArity}, received ${composition.args.size})", composition.position)
        )
    }

    private fun processCompositionArgs(args: List<Construction>, expected: List<Type>) = args
        .zip(expected)
        .map { (cons, expType) ->
            val processed = processConstruction(cons)

            when {
                expType !is Unknown && !match(expType, processed.constructedType) -> processed.withReport(
                    Report("Function argument type mismatch. " +
                            "Expected '${expType}', Got '${processed.constructedType}'", processed.position
                    )
                )
                else -> processed
            }
        }

    private val numericOperator = setOf("+", "-", "*", "/")

    private fun processCompositionWithNumOp(composition: Composition, op: TilFunction) = with(composition) {

        val processedArgs = processOperatorArgs(args)
        val arityErrors = checkArity(composition, 2)

        val isReal = processedArgs.isNotEmpty() && processedArgs.any { match(it.constructedType, Types.Real) }

        val isInt = !isReal && processedArgs.isNotEmpty() &&
                processedArgs.any { match(it.constructedType, Types.Int) }

        val opType = when {
            isReal -> Types.Real
            isInt  -> Types.Int
            else   -> Unknown
        }

        val fn = TilFunction(op.name, op.position, FunctionType(opType, opType, opType), listOf()).trivialize()

        Composition(fn, processedArgs, position, opType, reports + arityErrors)
    }

    private fun processCompositionWithNumOp(composition: Composition, fn: Construction) =
        processCompositionWithNumOp(composition, (fn as Trivialization).construction as TilFunction)

    private fun processCompositionWithFn(composition: Composition, fn: Construction) = with(composition) {
        val fnType = typeRepo.process(fn.constructedType as FunctionType)
        val fnArgs = fnType.argTypes

        val arityErrors = checkArity(composition, fnArgs.size)

        val processedArgs = args.map(::processConstruction) // processCompositionArgs(args, fnArgs)

        val genericTypes = fnType.argTypes.zip(processedArgs)
            .filter { (exp, _) -> exp is GenericType }
            .associate { (exp, rec) ->
                exp as GenericType
                exp.argNumber to rec.constructedType
            }

        val returnType = when (fnType.imageType) {
            is GenericType -> genericTypes[fnType.imageType.argNumber] ?: Unknown
            else           -> fnType.imageType
        }

        val returnTypeErrors = when {
            fnType.imageType is GenericType && fnType.imageType.argNumber !in genericTypes ->
                listOf(Report("Return type of generic function could not be deduced from type arguments", composition.position))
            else -> listOf()
        }

        val typeMatches = match(fnType, processedArgs.map { it.constructedType })

        val argsWithReports = processedArgs.zip(typeMatches).zip(fnType.argTypes)
            .map { (pair, exp) ->
                val (arg, typeMatch) = pair
                when (typeMatch) {
                    true -> arg
                    else -> arg.withReport(Report("Type mismatch in function application (expected: $exp, received: ${arg.constructedType})", arg.position))
                }
            }

        Composition(fn, argsWithReports, position, returnType, reports + arityErrors + returnTypeErrors)
    }

    // Double execution cannot be meaningfully type-checked
    // Thus, the typechecking is delegated to the user
    // It's on them for using double executions
    // To properly check double executions, we would need to be able to execute the procedures,
    // yet, TILScript in its current state lacks the ability to describe how such executions should be performed
    // TILisp may yet find more success in this regard
    private fun processCompositionWithDE(comp: Composition, fn: Construction) =
        Composition(fn, comp.args.map(::processConstruction), comp.position, Unknown, comp.reports)

    private fun processComposition(composition: Composition) = with(composition) {

        val fn = processCompositionFn(function)

        val isNumericOp = fn is Trivialization && fn.construction is TilFunction &&
                fn.construction.name in numericOperator

        when {
            isNumericOp          -> processCompositionWithNumOp(composition, fn)
            fn.isDoubleExecution -> processCompositionWithDE(composition, fn)
            else                 -> processCompositionWithFn(composition, fn)
        }
    }

    private fun processClosureForked(closure: Closure) = with(closure) {

        val vars = variables.map {

            val typed = when (it.constructedType) {
                !is Unknown -> it
                else -> it.assignType(typeRepo.process(lambdaBoundType(it.name)))
            }

            val checked = when (typed.constructedType) {
                is Unknown -> typed.withReport(Report("Type of lambda bound variable is undefined", typed.position))
                else -> typed
            }

            repo.define(checked)

            checked
        }

        val abstracted = process(construction, this@TypeChecker, typeRepo)

        Closure(vars, abstracted, position, reports=reports).assignType()
    }

    private fun processClosure(closure: Closure) = fork().processClosureForked(closure)

    private fun assignFnType(function: TilFunction) = findSymbolType(function.name).let { type ->
        when (type) {
            is FunctionType -> function.assignType(type)
            else -> function.withReport(Report("${function.name} is not a function", function.position))
        }
    }

    private fun processFunction(fn: TilFunction) = when (fn.constructedType) {
        !is Unknown -> fn
        else -> assignFnType(fn)
    }

    private fun processLiteral(literal: Value) = when {
        literal.constructedType !is Unknown -> literal
        literal is Symbol -> literal.assignType(findSymbolType(literal.value))
        else -> throw RuntimeException("Attempting to assign type to a non-symbol literal")
    }

    private fun processConstruction(construction: Construction): Construction = when (construction) {
        is Closure        -> processClosure(construction)
        is Composition    -> processComposition(construction)
        is Trivialization -> processTrivialization(construction)
        is Execution      -> processExecution(construction)
        is Variable       -> processVariable(construction)

        // The only way to reach this point is by attempting to execute literals or function names
        // The only legal way to mention a value or a function is to use a trivialization, and a trivialization
        // does not attempt to execute its argument
        is Value          -> construction.withReport(Report("Literals cannot be executed.", construction.position))
        is TilFunction    -> construction.withReport(Report("Functions cannot be executed.", construction.position))
    }

    private fun addSymbol(symbol: Symbol) = Symbol(
        symbol.value,
        symbol.position,
        typeRepo.process(symbol.constructedType),
        reports=symbol.reports
    ).apply {
        repo.define(this)
    }

    private fun processSingleDecl(symbol: Symbol) = when (symbol.value) {
        in repo -> {
            val currentType = repo[symbol.value]!!
            when {
                !match(currentType, symbol.constructedType) ->
                    symbol.withReport(Report("Redefinition of symbol '${symbol.value}' with a different type", symbol.position))
                else -> symbol
            }
        }
        else -> when (symbol.constructedType) {
            is GenericType ->
                symbol.withReport(Report("Generic types are only allowed in function definitions", symbol.position))
            else -> addSymbol(symbol)
        }
    }

    private fun processLiteralDeclaration(decl: LiteralDeclaration) = decl.apply {
        literals.forEach(::processSingleDecl)
    }

    private fun processTypeDefinition(def: TypeDefinition) = def.apply {
        when (alias.name) {
            in typeRepo -> {
                val currentType = typeRepo[alias.name]!!
                when {
                    !match(currentType, alias.type) ->
                        def.withReport(Report("Redefinition of type alias '${def.alias.name}' with a different type", def.position))
                    else -> alias
                }
            }
            else -> when (def.alias.type) {
                is GenericType ->
                    def.withReport(Report("Generic types are only allowed in function definitions", def.position))
                else -> typeRepo.process(def.alias)
            }
        }
    }

    private fun processSingleDecl(variable: Variable) = when (variable.name) {
        in repo -> {
            val currentType = repo[variable.name]!!
            when {
                !match(currentType, variable.constructedType) ->
                    variable.withReport(Report("Redefinition of variable '${variable.name}' with a different type", variable.position))
                else -> variable
            }
        }
        else -> when (variable.constructedType is GenericType) {
            true -> variable.withReport(Report("Generic types are only allowed in function definitions", variable.position))
            else -> variable.apply { repo.declare(this) }
        }

    }

    private fun processVariableDeclaration(decl: VariableDeclaration) = with(decl) {
        VariableDeclaration(variables.map(::processSingleDecl), position, reports)
    }

    private fun processSingleDecl(fn: TilFunction): TilFunction  {

        val genError = when (fn.constructedType is FunctionType && fn.constructedType.imageType is GenericType) {
            true -> {
                val genNums = fn.constructedType.argTypes.filterIsInstance<GenericType>().map { it.argNumber }.toSet()

                when (fn.constructedType.imageType.argNumber !in genNums) {
                    true -> fn.withReport(Report("Return type of generic function could not be deduced from type arguments", fn.position))

                    false -> fn
                }
            }

            else -> fn
        }

        return when (genError.name) {
            in repo -> {
                val currentType = repo[genError.name]!!
                when {
                    !match(currentType, genError.constructedType) ->
                        genError.withReport(Report("Redefinition of symbol '${genError.name}' with a different type", genError.position))
                    else -> genError
                }
            }
            else -> genError.apply { repo.declare(this) }
        }
    }

    private fun processFunctionDeclaration(decl: FunctionDeclaration) = with(decl) {
        FunctionDeclaration(functions.map(::processSingleDecl), position, reports)
    }

    private fun processFunctionDefinitionForked(def: FunctionDefinition): FunctionDefinition {
        val withReport = when  {
            repo.isDefined(def.name) ->
                def.withReport(Report("Redefinition of symbol '${def.name}' with a conflicting definition", def.position))
            def.name in repo -> {
                val currentType = repo[def.name]!!
                when {
                    !match(currentType, def.signature) ->
                        def.withReport(Report("Redefinition of symbol '${def.name}' with a different type", def.position))
                    else -> def
                }
            }
            else -> def.apply { repo.define(tilFunction) }
        }

        withReport.args.forEach(repo::define)

        val withConstruction = FunctionDefinition(
            withReport.name,
            withReport.args,
            withReport.constructsType,
            processConstruction(withReport.construction),
            withReport.position,
            withReport.reports,
        )

        val expType = withConstruction.construction.constructedType
        val received = withConstruction.constructsType

        val genArgNums = def.args.map { it.constructedType }.filterIsInstance<GenericType>().map { it.argNumber }.toSet()

        val genericsError = when {
            def.constructsType is GenericType && def.constructsType.argNumber !in genArgNums ->
                withConstruction.withReport(Report("Return type of generic function cannot be deduced from type arguments", withConstruction.position))
            else -> withConstruction
        }

        return when (match(expType, received)) {
            true -> genericsError
            else -> genericsError.withReport(
                Report(
                    "Type constructed by function body does not match function signature (expected: ${expType}, received: ${received})",
                    genericsError.construction.position)
            )
        }
    }

    private fun processFunctionDefinition(def: FunctionDefinition) =
        fork().processFunctionDefinitionForked(def)

    private fun processVariableDefinition(def: VariableDefinition): VariableDefinition {

        val withRedef = when {
            repo.isDefined(def.name) ->
                def.withReport(Report("Redefinition of symbol ${def.name} with a conflicting definition", def.position))
            repo.isDeclared(def.name) -> {
                val currentType = repo[def.name]!!
                when {
                    !match(currentType, def.constructsType) ->
                        def.withReport(Report("Redefinition of symbol ${def.name} with a different type", def.position))
                    else -> def
                }
            }
            else -> def
        }

        val withConstruction = VariableDefinition(
            withRedef.name,
            withRedef.constructsType,
            processConstruction(withRedef.construction),
            withRedef.position,
            withRedef.reports,
        )

        val withGenerics = when (withConstruction.constructsType) {
            is GenericType -> withConstruction.withReport(Report("Generic types are only allowed in function definitions", withConstruction.position))
            else -> withConstruction
        }

        return when (match(withGenerics.construction.constructedType, withGenerics.constructsType)) {
            true -> withGenerics
            else -> withGenerics.withReport(
                Report("Type constructed by variable initializer does not match declared type", withGenerics.construction.position)
            )
        }
    }

    private fun processDeclaration(declaration: Declaration): Declaration = when (declaration) {
        is LiteralDeclaration  -> processLiteralDeclaration(declaration)
        is TypeDefinition      -> processTypeDefinition(declaration)
        is VariableDeclaration -> processVariableDeclaration(declaration)
        is FunctionDeclaration -> processFunctionDeclaration(declaration)
        is FunctionDefinition  -> processFunctionDefinition(declaration)
        is VariableDefinition  -> processVariableDefinition(declaration)
    }

    private fun process(sentence: Sentence): Sentence = when (sentence) {
        is Declaration  -> processDeclaration(sentence)
        is Construction -> processConstruction(sentence)
    }

    fun process(sentences: Iterable<Sentence>): List<Sentence> = sentences.map(::process)

}
