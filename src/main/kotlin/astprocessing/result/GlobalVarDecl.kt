package org.fpeterek.til.interpreter.astprocessing.result

import org.fpeterek.til.interpreter.util.SrcPosition

class GlobalVarDecl(val vars: List<VarName>, val type: DataType, srcPos: SrcPosition) : IntermediateResult(srcPos)
