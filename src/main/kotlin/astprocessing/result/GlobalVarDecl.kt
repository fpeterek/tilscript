package org.fpeterek.til.typechecking.astprocessing.result

import org.fpeterek.til.typechecking.util.SrcPosition

class GlobalVarDecl(val vars: List<VarName>, val type: DataType, srcPos: SrcPosition) : IntermediateResult(srcPos)
