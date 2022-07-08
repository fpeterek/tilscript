package org.fpeterek.til.typechecking.interpreter

import org.fpeterek.til.typechecking.sentence.Construction
import org.fpeterek.til.typechecking.sentence.Nil
import org.fpeterek.til.typechecking.util.SrcPosition

interface InterpreterInterface {
    val nil: Nil get() = Nil(SrcPosition(-1, -1))

    fun interpret(construction: Construction): Construction
}