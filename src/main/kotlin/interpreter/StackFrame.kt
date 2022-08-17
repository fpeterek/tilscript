package org.fpeterek.tilscript.interpreter.interpreter

import org.fpeterek.tilscript.interpreter.sentence.Variable
import org.fpeterek.tilscript.interpreter.util.die

class StackFrame(val parent: StackFrame?) {

    private val variables = mutableMapOf<String, Variable>()

    fun putVar(variable: Variable) {
        variables[variable.name] = variable
    }

    fun getVar(name: String): Variable = variables.getOrElse(name) {
        die("No such variable '$name'")
    }

    fun hasVar(name: String) = name in variables

    operator fun contains(name: String) = hasVar(name)

    operator fun get(name: String) = variables[name]

}
