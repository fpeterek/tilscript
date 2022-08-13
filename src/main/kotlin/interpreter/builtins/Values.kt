package org.fpeterek.tilscript.interpreter.interpreter.builtins

import org.fpeterek.tilscript.interpreter.sentence.Bool
import org.fpeterek.tilscript.interpreter.sentence.Nil as NilObject
import org.fpeterek.tilscript.interpreter.util.SrcPosition

object Values {
    val True = Bool(true, SrcPosition(-1, -1))
    val False = Bool(false, SrcPosition(-1, -1))

    val Nil = NilObject(SrcPosition(-1, -1))

    val all = listOf(True, False, Nil)
}