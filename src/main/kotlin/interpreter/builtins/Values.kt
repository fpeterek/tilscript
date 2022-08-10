package org.fpeterek.til.interpreter.interpreter.builtins

import org.fpeterek.til.interpreter.sentence.Bool
import org.fpeterek.til.interpreter.sentence.Nil as NilObject
import org.fpeterek.til.interpreter.util.SrcPosition

object Values {
    val True = Bool(true, SrcPosition(-1, -1))
    val False = Bool(false, SrcPosition(-1, -1))

    val Nil = NilObject(SrcPosition(-1, -1))

    val all = listOf(True, False, Nil)
}