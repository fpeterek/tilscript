package org.fpeterek.tilscript.interpreter.astprocessing.result

import org.fpeterek.tilscript.common.SrcPosition

class TypedVar(
    val name: String,
    val type: DataType?,
    srcPos: SrcPosition
) : IntermediateResult(srcPos)
