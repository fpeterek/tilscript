package org.fpeterek.til.interpreter.interpreter.interpreterinterface

import org.fpeterek.til.interpreter.sentence.Construction
import org.fpeterek.til.interpreter.sentence.Nil
import org.fpeterek.til.interpreter.sentence.Variable
import org.fpeterek.til.interpreter.util.SrcPosition
import org.fpeterek.til.interpreter.types.Type


interface InterpreterInterface {
    val nil: Nil get() = Nil(SrcPosition(-1, -1))

    fun interpret(construction: Construction): Construction

    fun typesMatch(t1: Type, t2: Type): Boolean

    fun ensureMatch(expected: Type, received: Type)

    fun createLocal(variable: Variable, value: Construction)
}