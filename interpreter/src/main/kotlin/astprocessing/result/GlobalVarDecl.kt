package org.fpeterek.tilscript.interpreter.astprocessing.result

import org.fpeterek.tilscript.common.SrcPosition

class GlobalVarDecl(val vars: List<VarName>, val type: DataType, srcPos: SrcPosition) : IntermediateResult(srcPos)
