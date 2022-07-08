package org.fpeterek.til.typechecking.interpreter

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

    override fun interpret(construction: Construction): Construction = when (construction) {
        is Closure -> TODO()
        is Composition -> TODO()
        is Execution -> TODO()
        is TilFunction -> TODO()
        is Trivialization -> TODO()
        // Values cannot be executed as they by themselves do not construct anything
        // Nil also only ever constructs nil, but Nil is a Value
        is Value -> nil
        is Variable -> interpret(construction)
    }


    fun interpret(sentence: Sentence) {

    }

    fun interpret(sentences: Iterable<Sentence>) {

    }

}
