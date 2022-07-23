package org.fpeterek.til.typechecking.interpreter

import org.fpeterek.til.typechecking.sentence.Construction
import org.fpeterek.til.typechecking.sentence.Variable

class StackFrame(val parent: StackFrame?) {

    private val variables = mutableMapOf<String, Variable>()

    fun putVar(variable: Variable) {
        variables[variable.name] = variable
    }

    fun getVar(name: String): Construction = variables.getOrElse(name) {
        throw RuntimeException("No such variable '$name'")
    }

    fun hasVar(name: String) = name in variables

    operator fun contains(name: String) = hasVar(name)

    operator fun get(name: String) = variables[name]

}
