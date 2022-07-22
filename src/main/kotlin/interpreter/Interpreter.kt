package org.fpeterek.til.typechecking.interpreter

import org.fpeterek.til.typechecking.interpreter.interpreterinterface.InterpreterInterface
import org.fpeterek.til.typechecking.sentence.*
import org.fpeterek.til.typechecking.typechecker.TypeMatcher
import org.fpeterek.til.typechecking.types.SymbolRepository
import org.fpeterek.til.typechecking.types.Type
import org.fpeterek.til.typechecking.types.TypeRepository


class Interpreter: InterpreterInterface {

    private val symbolRepo = SymbolRepository()
    private val typeRepo = TypeRepository()

    private val topLevelFrame = StackFrame(parent = null)

    private val stack: MutableList<StackFrame> = mutableListOf(topLevelFrame)

    private val currentFrame get() = stack.last()

    private fun pushFrame() = stack.add(StackFrame(parent = currentFrame))
    private fun popFrame() = stack.removeLast()

    private fun <T> withFrame(fn: () -> T): T {
        pushFrame()
        val result = fn()
        popFrame()
        return result
    }

    private infix fun Type.matches(other: Type) = TypeMatcher.match(this, other, typeRepo)

    private fun findVar(frame: StackFrame?, name: String): Variable = when {
        frame == null -> throw RuntimeException("Variable not found '$name'")
        name in frame -> frame[name]!!
        else          -> findVar(frame.parent, name)
    }

    private fun findVar(name: String) = findVar(currentFrame, name)

    private fun interpret(variable: Variable): Construction {

        if (variable.value == null) {
            throw RuntimeException("Variable '${variable.name}' is declared but undefined")
        }

        val frameVar = findVar(variable.name)

        if (!(frameVar.constructedType matches variable.constructedType)) {
            throw RuntimeException("Mismatch between expected type (${variable.constructedType}) and actual type of variable (${frameVar.constructedType})")
        }

        return variable.value
    }

    private fun interpret(triv: Trivialization) = triv.construction

    private fun execute(construction: Construction, executions: Int): Construction = if (executions > 0) {
        execute(interpret(construction), executions-1)
    } else {
        construction
    }

    private fun interpret(execution: Execution) = execute(execution.construction, execution.executionOrder)

    private fun createLambdaCapture(closure: Closure) = LambdaContext(
        LambdaCaptureCreator(currentFrame).captureVars(closure.construction)
    )

    private fun interpret(closure: Closure): TilFunction = withFrame {
        // We want to put variables introduced by the closure on the stack even if we aren't calling the
        // resulting function as of now to avoid capturing variables with the same name from a higher scope
        // This is necessary because we use the call stack to create captures
        closure.variables.forEach(currentFrame::putVar)

        TilFunction(
            "<Lambda>",
            closure.position,
            closure.constructedType,
            closure.reports,
            LambdaFunction(closure.variables, closure.construction, createLambdaCapture(closure)),
        )
    }

    private fun interpret(comp: Composition): Construction {
        val fn = interpret(comp.function)

        if (fn !is TilFunction) {
            throw RuntimeException("Only functions can be applied on arguments. $fn is not a function")
        }

        if (fn.implementation == null) {
            throw RuntimeException("Function ${fn.name} is declared but undefined, application is impossible")
        }

        return withFrame {
            fn.implementation.apply(this, comp.args)
        }
    }

    // TODO: Test
    override fun interpret(construction: Construction): Construction = when (construction) {
        is Closure        -> interpret(construction)
        is Composition    -> interpret(construction)
        is Execution      -> interpret(construction)
        is Trivialization -> interpret(construction)
        // Values cannot be executed as they by themselves do not construct anything
        // Nil also only ever constructs nil, but Nil is a Value
        is Value          -> nil
        // Functions too cannot be executed, functions can only be applied using compositions
        // Functions must be constructed using trivializations or closures
        is TilFunction    -> nil
        is Variable       -> interpret(construction)
    }

    override fun typesMatch(t1: Type, t2: Type) = t1 matches t2

    override fun ensureMatch(expected: Type, received: Type) {
        if (!(expected matches received)) {
            throw RuntimeException("Type mismatch (expected: $expected, received: $received)")
        }
    }

    override fun createLocal(variable: Variable, value: Construction) {
        val varWithValue = Variable(
            variable.name,
            variable.position,
            variable.constructedType,
            variable.reports,
            value,
        )

        currentFrame.putVar(varWithValue)
    }

    // TODO: Declarations
    private fun interpret(declaration: Declaration) = Unit

    fun interpret(sentence: Sentence) {
        when (sentence) {
            is Construction -> interpret(sentence)
            is Declaration  -> interpret(sentence)
        }
    }

    fun interpret(sentences: Iterable<Sentence>) = sentences.forEach {
        try {
            interpret(it)
        } catch (e: Exception) {
            println("Runtime error: ${e.message}")
            return@forEach
        }
    }

}
