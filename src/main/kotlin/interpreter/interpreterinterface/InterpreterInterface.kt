package org.fpeterek.til.typechecking.interpreter.interpreterinterface

import org.fpeterek.til.typechecking.sentence.Construction
import org.fpeterek.til.typechecking.sentence.Nil
import org.fpeterek.til.typechecking.sentence.Variable
import org.fpeterek.til.typechecking.util.SrcPosition
import org.fpeterek.til.typechecking.types.Type


interface InterpreterInterface {
    val nil: Nil get() = Nil(SrcPosition(-1, -1))

    fun interpret(construction: Construction): Construction

    fun typesMatch(t1: Type, t2: Type): Boolean

    fun ensureMatch(expected: Type, received: Type)

    fun createLocal(variable: Variable, value: Construction)
}