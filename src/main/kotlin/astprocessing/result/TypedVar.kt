package org.fpeterek.til.interpreter.astprocessing.result

import org.fpeterek.til.interpreter.util.SrcPosition

class TypedVar(
    val name: String,
    val type: DataType?,
    srcPos: SrcPosition
) : IntermediateResult(srcPos)
