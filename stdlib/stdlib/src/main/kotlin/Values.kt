package org.fpeterek.tilscript.stdlib

import org.fpeterek.tilscript.common.sentence.Bool
import org.fpeterek.tilscript.common.sentence.Nil as NilObject
import org.fpeterek.tilscript.common.SrcPosition

object Values {
    val True = Bool(true, SrcPosition(-1, -1))
    val False = Bool(false, SrcPosition(-1, -1))

    val Nil = NilObject(SrcPosition(-1, -1))

    val all = listOf(True, False, Nil)
}