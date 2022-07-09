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

    private val stack = mutableListOf(StackFrame())

    private fun pushFrame() = stack.add(0, StackFrame())
    private fun popFrame() = stack.removeFirst()

    private infix fun Type.matches(other: Type) = TypeMatcher.match(this, other, typeRepo)

    private fun findVar(name: String): Construction {
        val frame = stack.firstOrNull { it.hasVar(name) } ?: throw RuntimeException("Variable not found '$name'")

        return frame.getVar(name)
    }

    private fun interpret(variable: Variable): Construction {
        val value = findVar(variable.name)

        if (!(value.constructedType matches variable.constructedType)) {
            throw RuntimeException("Mismatch between expected type (${variable.constructedType}) and actual type of variable (${value.constructedType})")
        }

        return value
    }

    private fun interpret(triv: Trivialization) = triv.construction

    private fun execute(construction: Construction, executions: Int): Construction = if (executions > 0) {
        execute(interpret(construction), executions-1)
    } else {
        construction
    }

    private fun interpret(execution: Execution) = execute(execution.construction, execution.executionOrder)

    override fun interpret(construction: Construction): Construction = when (construction) {
        is Closure        -> TODO()
        is Composition    -> TODO()
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


    fun interpret(sentence: Sentence) {

    }

    fun interpret(sentences: Iterable<Sentence>) {

    }

}
