package org.fpeterek.til.interpreter.interpreter

import org.fpeterek.til.interpreter.sentence.Construction
import org.fpeterek.til.interpreter.sentence.Variable

class StackFrame(val parent: StackFrame?) {

    private val variables = mutableMapOf<String, Variable>()

    fun putVar(variable: Variable) {
        variables[variable.name] = variable
    }

    fun getVar(name: String): Variable = variables.getOrElse(name) {
        throw RuntimeException("No such variable '$name'")
    }

    fun hasVar(name: String) = name in variables

    operator fun contains(name: String) = hasVar(name)

    operator fun get(name: String) = variables[name]

}
