package org.fpeterek.til.typechecking.typechecker

import org.fpeterek.til.typechecking.constructions.*
import org.fpeterek.til.typechecking.typechecker.TypeAssignment.assignType
import org.fpeterek.til.typechecking.types.ConstructionType
import org.fpeterek.til.typechecking.types.FunctionType
import org.fpeterek.til.typechecking.types.Type
import org.fpeterek.til.typechecking.types.Unknown
import org.fpeterek.til.typechecking.util.SymbolRepository
import org.fpeterek.til.typechecking.util.Util.incrementOrder
import org.fpeterek.til.typechecking.util.Util.trivialize

class TypeChecker private constructor(
    private val parent: TypeChecker?,
    private val repo: SymbolRepository = SymbolRepository()
) {

    companion object {
        fun process(construction: Construction, symbolRepository: SymbolRepository) =
            TypeChecker(null, symbolRepository).process(construction)

        private fun process(construction: Construction, parent: TypeChecker?) =
            TypeChecker(parent).process(construction)
    }

    // Search in local repo first
    // If symbol is not found, search in parent repo
    // If symbol is not found ever still, and we have reached the outermost scope,
    // return Unknown and hope the type can be inferred
    private fun findSymbolType(symbol: String): Type =
        repo[symbol] ?: parent?.findSymbolType(symbol) ?: Unknown

    private val outermostRepo: SymbolRepository
        get() = parent?.outermostRepo ?: repo

    private fun processTrivialization(trivialization: Trivialization): Trivialization {

        val processed = when (trivialization.construction) {
            is Variable, is TilFunction, is Literal ->
                process(trivialization.construction, this)

            // Binding by trivialization -> variables from the outside
            // are inaccessible
            else -> process(trivialization.construction, outermostRepo)
        }.trivialize()

        // We use constructedType to store the type of literals and functions
        // even though literals (i.e. values from base) and functions themselves do not
        // construct anything
        // To clarify, functions must be applied onto (zero or more) arguments using compositions,
        // to construct anything
        val type = when (trivialization.construction) {
            is Literal, is TilFunction -> trivialization.construction.constructedType

            // Otherwise, trivialization constructs a construction of a certain order
            else -> trivialization.construction.constructionType
        }

        return processed.assignType(type)
    }

    private fun processVariable(variable: Variable) =
        variable.assignType(findSymbolType(variable.name))

    private fun processExecution(execution: Execution) = with(execution) {

        if (construction !is Composition) {
            throw RuntimeException("Only compositions can be executed")
        }

        val processedConstruction = process(construction)
        val firstExecution = Execution(processedConstruction, executionOrder).assignType()

        if (executionOrder == 1) {
            firstExecution
        } else {
            firstExecution.construction as Composition
            if (firstExecution.construction.constructedType !is ConstructionType) {
                throw RuntimeException("")
            }
            // Unknown type as we may not be able to determine the type constructed
            // by the constructed construction
            // Yes, things start to get somewhat convoluted at this point
            Execution(
                processedConstruction,
                2,
                constructionType=processedConstruction.constructionType.incrementOrder())
        }
    }

    private fun execute(construction: Construction) = when (construction) {
        is Literal -> throw RuntimeException("Literals cannot be executed.")
        is TilFunction -> throw RuntimeException("Functions cannot be executed.")
        else -> process(construction)
    }

    private fun processCompositionFn(fn: Construction) = process(fn).let { processed ->
        when {
            processed is TilFunction ->
                throw RuntimeException(
                    "Functions cannot be executed, did you forget a trivialization ('${processed.name})?")
            processed.constructedType !is FunctionType ->
                throw RuntimeException("Only functions can be applied on arguments using a composition")
        }

        processed
    }

    private fun storeVarType(variable: Variable) {
        if (variable.name in repo) {
            repo.add(variable)
        } else {
            parent?.storeVarType(variable)
        }
    }

    private fun assignVarType(variable: Variable, type: Type) =
        variable.assignType(type).apply(::storeVarType)

    private fun processCompositionArgs(args: List<Construction>, expected: List<Type>) =
        args.zip(expected).map { (cons, expType) ->
            val processed = execute(cons)

            if (expType !is Unknown && expType != processed.constructedType) {
                throw RuntimeException("Function argument type mismatch. " +
                        "Expected '${expType}', Got '${processed.constructedType}'")
            }

            if (processed.constructedType is Unknown) {
                when (processed) {
                    is Variable -> assignVarType(processed, expType)
                    else -> throw NotImplementedError()
                }
            } else {
                processed
            }
        }

    private fun inferTypes(actualArgs: List<Construction>, fnArgs: List<Type>) =
        fnArgs.zip(actualArgs).map { (arg, received) ->
            when (arg) {
                is Unknown -> received.constructedType
                else -> arg
            }
        }


    private fun processComposition(composition: Composition) = with(composition) {

        val fn = processCompositionFn(function)
        val fnType = fn.constructedType as FunctionType
        val fnArgs = fnType.argTypes

        val processedArgs = processCompositionArgs(args, fnArgs)

        val argsMaxType = processedArgs.maxByOrNull { it.constructionType.order }?.constructionType

        val inferredTypes = inferTypes(processedArgs, fnArgs)

        val consType = when {
            argsMaxType == null -> fn.constructionType
            argsMaxType.order > fn.constructionType.order -> argsMaxType
            else -> fn.constructionType
        }

        Composition(fn, processedArgs, fnType.imageType, consType)
    }

    private fun processClosure(closure: Closure) = with(closure) {
        variables.forEach {
            repo.add(it)
        }

        val composition = when (construction) {
            is Composition -> processComposition(construction)
            else -> throw RuntimeException("A closure must be an abstraction over a composition")
        }

        // We assume the variable types might have been inferred when processing
        // the composition, thus we look through the local repo and check whether
        // the types of lambda parameters are known already
        // We only check the local repo, which should only contains variables introduced
        // by the current lambda abstraction, as they were added a couple of lines earlier
        // in this very function
        // Lambda abstractions can shadow variables introduced earlier and if we were
        // to check parent repositories, we could accidentally reassign the type of a completely
        // different variable
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

    // Identity function, no action is necessary now, though that may change in the future
    private fun processLiteral(literal: Literal) = literal

    fun process(construction: Construction): Construction = when (construction) {
        is Closure -> processClosure(construction)
        is Composition -> processComposition(construction)
        is Trivialization -> processTrivialization(construction)
        is Execution -> processExecution(construction)
        is Variable -> processVariable(construction)
        is TilFunction -> processFunction(construction)
        is Literal -> processLiteral(construction)
    }

}
