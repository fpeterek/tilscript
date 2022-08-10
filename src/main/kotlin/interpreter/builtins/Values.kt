package org.fpeterek.til.typechecking.interpreter.builtins

import org.fpeterek.til.typechecking.sentence.Bool
import org.fpeterek.til.typechecking.sentence.Nil as NilObject
import org.fpeterek.til.typechecking.util.SrcPosition

object Values {
    val True = Bool(true, SrcPosition(-1, -1))
    val False = Bool(false, SrcPosition(-1, -1))

    val Nil = NilObject(SrcPosition(-1, -1))

    val all = listOf(True, False, Nil)
}