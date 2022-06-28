package org.fpeterek.til.typechecking.typechecker


import org.fpeterek.til.typechecking.reporting.Report
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

    private val Construction.isDoubleExecution
        get() = this is Execution && executionOrder == 2

    private fun match(l: Type, r: Type) = l is Unknown || r is Unknown || TypeMatcher.match(l, r, typeRepo)

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

    private fun execute(construction: Construction) = when (construction) {
        is Literal -> construction.withReport(Report("Literals cannot be executed.", construction.position))
        is TilFunction -> construction.withReport(Report("Functions cannot be executed.", construction.position))
        else -> processConstruction(construction)
    }

    private fun processCompositionFn(fn: Construction) = processConstruction(fn).let { processed ->
        when {
            processed is TilFunction -> processed.withReport(
                Report("Functions cannot be executed, did you forget a trivialization?", processed.position)
            )
            processed.constructedType !is FunctionType -> processed.withReport(
                Report("Only functions can be applied on arguments using a composition", processed.position)
            )
            else -> processed
        }
    }

    private fun processOperatorArgs(args: List<Construction>): List<Construction> {
        val isInt = match(execute(args.first()).constructedType, Builtins.Nu)
        val expType = when {
            isInt -> Builtins.Nu
            else -> Builtins.Eta
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
            val processed = execute(cons)

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

        val isInt = processedArgs.isNotEmpty() && match(processedArgs.first().constructedType, Builtins.Nu)
        val opType = when {
            isInt -> Builtins.Nu
            else -> Builtins.Eta
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

        val processedArgs = processCompositionArgs(args, fnArgs)

        Composition(fn, processedArgs, position, fnType.imageType, reports + arityErrors)
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

            repo.add(checked)

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

    private fun processLiteral(literal: Literal) = when (literal.constructedType) {
        !is Unknown -> literal
        else -> literal.assignType(findSymbolType(literal.value))
    }

    private fun processConstruction(construction: Construction): Construction = when (construction) {
        is Closure        -> processClosure(construction)
        is Composition    -> processComposition(construction)
        is Trivialization -> processTrivialization(construction)
        is Execution      -> processExecution(construction)
        is Variable       -> processVariable(construction)
        is TilFunction    -> processFunction(construction)
        is Literal        -> processLiteral(construction)
    }

    private fun addLiteral(lit: Literal) = Literal(
        lit.value,
        lit.position,
        typeRepo.process(lit.constructedType),
        reports=lit.reports
    ).apply {
        repo.add(this)
    }

    private fun processSingleDecl(lit: Literal) = when (lit.value) {
        in repo -> lit.withReport(Report("Redefinition of symbol '${lit.value}'", lit.position))
        else -> addLiteral(lit)
    }

    private fun processLiteralDeclaration(decl: LiteralDeclaration) = decl.apply {
        literals.forEach(::processSingleDecl)
    }

    private fun processTypeDefinition(def: TypeDefinition) = def.apply {
        when (alias.name) {
            in typeRepo -> def.withReport(Report("Redefinition of type '${def.alias.name}'", def.position))
            else -> typeRepo.process(def.alias)
        }
    }

    private fun processSingleDecl(variable: Variable) = when (variable.name) {
        in repo -> variable.withReport(Report("Redefinition of symbol '${variable.name}'", variable.position))
        else -> variable.apply { repo.add(this) }

    }

    private fun processVariableDeclaration(decl: VariableDeclaration) = with(decl) {
        VariableDeclaration(variables.map(::processSingleDecl), position, reports)
    }

    private fun processSingleDecl(fn: TilFunction) = when (fn.name) {
        in repo -> fn.withReport(Report("Redefinition of symbol '${fn.name}'", fn.position))
        else -> fn.apply { repo.add(this) }
    }

    private fun processFunctionDeclaration(decl: FunctionDeclaration) = with(decl) {
        FunctionDeclaration(functions.map(::processSingleDecl), position, reports)
    }

    private fun processFunctionDefinitionForked(def: FunctionDefinition): FunctionDefinition {
        val withReport = when (def.name) {
            in repo -> def.withReport(Report("Redefinition of symbol '${def.name}'", def.position))
            else -> def.apply { repo.add(tilFunction) }
        }

        withReport.args.forEach(repo::add)

        val withConstruction = FunctionDefinition(
            withReport.name,
            withReport.args,
            withReport.constructsType,
            processConstruction(withReport.construction),
            withReport.position,
            withReport.reports,
            withReport.context
        )

        return when (match(withConstruction.construction.constructedType, withConstruction.constructsType)) {
            true -> withConstruction
            else -> withConstruction.withReport(
                Report("Type constructed by function body does not match function signature", withConstruction.construction.position)
            )
        }
    }

    private fun processFunctionDefinition(def: FunctionDefinition) =
        fork().processFunctionDefinitionForked(def)

    private fun processVariableDefinition(def: VariableDefinition): VariableDefinition {

        val withRedef = when (def.name) {
            in repo -> def.withReport(Report("Redefinition of symbol ${def.name}", def.position))
            else    -> def
        }

        val withConstruction = VariableDefinition(
            withRedef.name,
            withRedef.constructsType,
            processConstruction(withRedef.construction),
            withRedef.position,
            withRedef.reports,
            withRedef.context
        )

        return when (match(withConstruction.construction.constructedType, withConstruction.constructsType)) {
            true -> withConstruction
            else -> withConstruction.withReport(
                Report("Type constructed by initializer does not match declared type", withConstruction.construction.position)
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
