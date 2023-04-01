package org.fpeterek.tilscript.math

import org.fpeterek.tilscript.common.SrcPosition
import org.fpeterek.tilscript.common.sentence.Real
import org.fpeterek.tilscript.common.sentence.Variable
import org.fpeterek.tilscript.stdlib.Types

import kotlin.math.E
import kotlin.math.PI

val e = Variable(
    "e",
    SrcPosition(-1, -1),
    type = Types.Real,
    value = Real(E, SrcPosition(-1, -1))
)

val pi = Variable(
    "pi",
    SrcPosition(-1, -1),
    type = Types.Real,
    value = Real(PI, SrcPosition(-1, -1))
)
