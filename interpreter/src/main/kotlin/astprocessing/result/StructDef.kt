package org.fpeterek.tilscript.interpreter.astprocessing.result

import org.fpeterek.tilscript.common.SrcPosition

class StructDef(
    val name: String,
    val vars: List<TypedVar>,
    srcPos: SrcPosition
) : IntermediateResult(srcPos)
