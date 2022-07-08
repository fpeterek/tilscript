package org.fpeterek.til.typechecking.interpreter

import org.fpeterek.til.typechecking.sentence.Construction

class StackFrame {

    private val variables = mutableMapOf<String, Construction>()

    fun putVar(name: String, value: Construction) {
        if (name in variables) {
            throw RuntimeException("Redefinition of variable '$name'")
        }
        variables[name] = value
    }

    fun getVar(name: String): Construction = variables.getOrElse(name) {
        throw RuntimeException("No such variable '$name'")
    }

    fun hasVar(name: String) = name in variables

}
