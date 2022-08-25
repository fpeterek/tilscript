package org.fpeterek.tilscript.interpreter.interpreter.interpreterinterface

import org.fpeterek.tilscript.interpreter.sentence.Construction
import org.fpeterek.tilscript.interpreter.sentence.Nil
import org.fpeterek.tilscript.interpreter.sentence.Variable
import org.fpeterek.tilscript.interpreter.types.FunctionType
import org.fpeterek.tilscript.interpreter.util.SrcPosition
import org.fpeterek.tilscript.interpreter.types.Type


interface InterpreterInterface {
    val nil: Nil get() = Nil(SrcPosition(-1, -1))

    fun interpret(construction: Construction): Construction

    fun typesMatch(t1: Type, t2: Type): Boolean

    fun fnArgsMatch(fn: FunctionType, types: List<Type>): List<Boolean>

    fun fnSignatureMatch(fn: FunctionType, returned: Type, args: List<Type>): Pair<Boolean, List<Boolean>>

    fun ensureMatch(expected: Type, received: Type)

    fun createLocal(variable: Variable, value: Construction)

    fun getVariable(name: String): Variable?
}