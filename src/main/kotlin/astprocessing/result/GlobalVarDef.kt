package org.fpeterek.til.interpreter.astprocessing.result

import org.fpeterek.til.interpreter.util.SrcPosition

class GlobalVarDef(
    val varName: VarName,
    val type: DataType,
    val init: Construction,
    srcPos: SrcPosition
) : IntermediateResult(srcPos)
