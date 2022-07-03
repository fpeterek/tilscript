package org.fpeterek.til.typechecking.contextrecognition

import org.fpeterek.til.typechecking.sentence.*

class ContextRecognizer private constructor(private val highestContext: Context) {

    companion object {
        fun assignContext(sentence: Sentence): Sentence = defaultInstance.assign(sentence)
        fun assignContext(sentences: Iterable<Sentence>): List<Sentence> = sentences.map(::assignContext)

        private fun assignContext(cl: Closure) = defaultInstance.assign(cl)
        private fun assignContext(comp: Composition) = defaultInstance.assign(comp)
        private fun assignContext(exec: Execution) = defaultInstance.assign(exec)
        private fun assignContext(lit: Literal) = defaultInstance.assign(lit)
        private fun assignContext(fn: TilFunction) = defaultInstance.assign(fn)
        private fun assignContext(triv: Trivialization) = defaultInstance.assign(triv)
        private fun assignContext(variable: Variable) = defaultInstance.assign(variable)
        private fun assignContext(cons: Construction) = defaultInstance.assign(cons)

        private val defaultInstance get() = ContextRecognizer(Context.Unknown)
    }

    private fun assign(cl: Closure) = Closure(
        cl.variables.map { it.withContext(highestContext) },
        ContextRecognizer(highestContext).assign(cl.construction),
        cl.position,
        cl.constructedType,
        cl.reports,
        maxOf(highestContext, Context.Intensional),
    )

    private fun assign(comp: Composition): Composition {

        val ctx = maxOf(highestContext, Context.Extensional)

        val fnContext = maxOf(
            when {
                comp.args.isEmpty() -> Context.Intensional
                else -> Context.Extensional
            }, ctx
        )

        val processedFn = when (comp.function) {
            is Trivialization -> when (comp.function.construction) {
                is TilFunction -> Trivialization(
                    comp.function.construction.withContext(fnContext),
                    comp.function.position,
                    comp.function.constructedType,
                    comp.function.constructionType,
                    comp.function.reports,
                    fnContext
                )
                else -> throw RuntimeException("Invalid state")
            }

            is Closure, is Composition ->
                ContextRecognizer(ctx).assign(comp.function).withContext(fnContext)

            is Variable -> comp.function.withContext(fnContext)

            else -> {
                println(comp)
                throw RuntimeException("Invalid state")
            }
        }

        val processedArgs = comp.args.map { ContextRecognizer(ctx).assign(it) }

        return Composition(
            processedFn, processedArgs, comp.position, comp.constructedType, comp.reports, ctx,
        )
    }

    private fun assign(exec: Execution) = Execution(
        ContextRecognizer(maxOf(highestContext, Context.Extensional)).assign(exec.construction),
        exec.executionOrder,
        exec.position,
        exec.constructedType,
        exec.reports,
        maxOf(highestContext, Context.Extensional),
    )

    private fun assign(nil: Nil) = Nil(
        nil.position,
        nil.constructedType,
        nil.reports,
        maxOf(highestContext, Context.Intensional)
    )

    private fun assign(bool: Bool) = Bool(
        bool.value,
        bool.position,
        bool.constructedType,
        bool.reports,
        maxOf(highestContext, Context.Intensional)
    )

    private fun assign(symbol: Symbol) = Symbol(
        symbol.value,
        symbol.position,
        symbol.constructedType,
        symbol.reports,
        maxOf(highestContext, Context.Intensional),
    )

    private fun assign(int: Integral) = Integral(
        int.value,
        int.position,
        int.constructedType,
        int.reports,
        maxOf(highestContext, Context.Intensional),
    )

    private fun assign(real: Real) = Real(
        real.value,
        real.position,
        real.constructedType,
        real.reports,
        maxOf(highestContext, Context.Intensional),
    )


    private fun assign(lit: Literal) = when (lit) {
        is Symbol   -> assign(lit)
        is Bool     -> assign(lit)
        is Integral -> assign(lit)
        is Nil      -> assign(lit)
        is Real     -> assign(lit)
    }

    // Functions in compositions are handled separately - thus, we can assume
    // that if we have gotten to this point, the function is not applied to arguments
    // using a composition, and thus, it must appear in at least intensional context
    private fun assign(fn: TilFunction) = TilFunction(
        fn.name,
        fn.position,
        fn.constructedType,
        fn.reports,
        maxOf(Context.Intensional, highestContext)
    )

    // Same point as above for trivializations of functions
    private fun assign(triv: Trivialization) = Trivialization(
        when (triv.construction) {
            is Literal -> ContextRecognizer(maxOf(highestContext, Context.Intensional))
            is TilFunction -> ContextRecognizer(maxOf(highestContext, Context.Intensional))
            else -> ContextRecognizer(Context.Hyperintensional)
        }.assign(triv.construction),
        triv.position,
        triv.constructedType,
        triv.constructionType,
        triv.reports,
        maxOf(highestContext, Context.Intensional),
    )

    private fun assign(variable: Variable) = Variable(
        variable.name,
        variable.position,
        variable.constructedType,
        variable.reports,
        maxOf(highestContext, Context.Intensional),
    )

    private fun assign(def: Declaration): Declaration = def.withContext(Context.Definition)

    private fun assign(cons: Construction): Construction = when (cons) {
        is Closure        -> assign(cons)
        is Composition    -> assign(cons)
        is Execution      -> assign(cons)
        is Literal        -> assign(cons)
        is TilFunction    -> assign(cons)
        is Trivialization -> assign(cons)
        is Variable       -> assign(cons)
    }

    private fun assign(sentence: Sentence): Sentence = when (sentence) {
        is Declaration   -> assign(sentence)
        is Construction -> assign(sentence)
    }
}
