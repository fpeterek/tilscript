package org.fpeterek.tilscript.interpreter.astprocessing.result

import org.fpeterek.tilscript.common.SrcPosition

class GlobalVarDef(
    val varName: VarName,
    val type: DataType,
    val init: Construction,
    srcPos: SrcPosition
) : IntermediateResult(srcPos)
