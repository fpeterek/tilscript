package org.fpeterek.tilscript.interpreter.astprocessing.result

import org.fpeterek.tilscript.interpreter.util.SrcPosition

class TypedVar(
    val name: String,
    val type: DataType?,
    srcPos: SrcPosition
) : IntermediateResult(srcPos)
