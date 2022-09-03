package org.fpeterek.tilscript.interpreter.interpreter

import org.fpeterek.tilscript.common.interpreterinterface.StackFrameInterface
import org.fpeterek.tilscript.common.sentence.Variable
import org.fpeterek.tilscript.common.die

class StackFrame(override val parent: StackFrameInterface?): StackFrameInterface {

    private val variables = mutableMapOf<String, Variable>()

    override fun putVar(variable: Variable) {
        variables[variable.name] = variable
    }

    override fun getVar(name: String): Variable = variables.getOrElse(name) {
        die("No such variable '$name'")
    }

    override fun hasVar(name: String) = name in variables

    override operator fun contains(name: String) = hasVar(name)

    override operator fun get(name: String) = variables[name]

}
