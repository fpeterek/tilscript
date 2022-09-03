package org.fpeterek.tilscript.common.interpreterinterface

import org.fpeterek.tilscript.common.sentence.Variable

interface StackFrameInterface {

    val parent: StackFrameInterface?

    fun putVar(variable: Variable)

    fun getVar(name: String): Variable

    fun hasVar(name: String): Boolean

    operator fun contains(name: String) = hasVar(name)

    operator fun get(name: String): Variable?
}