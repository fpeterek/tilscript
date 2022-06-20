package org.fpeterek.til.typechecking.astprocessing.result

import org.fpeterek.til.typechecking.util.SrcPosition

class GlobalVarDef(
    val varName: VarName,
    val type: DataType,
    val init: Construction,
    srcPos: SrcPosition
) : IntermediateResult(srcPos)
