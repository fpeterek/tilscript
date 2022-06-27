package org.fpeterek.til.typechecking.astprocessing.result

import org.fpeterek.til.typechecking.util.SrcPosition

class TypedVar(
    val name: String,
    val type: DataType?,
    srcPos: SrcPosition
) : IntermediateResult(srcPos)
