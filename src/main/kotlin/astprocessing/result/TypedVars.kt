package org.fpeterek.til.typechecking.astprocessing.result

import org.fpeterek.til.typechecking.util.SrcPosition

class TypedVars(val vars: List<TypedVar>, srcPos: SrcPosition) : IntermediateResult(srcPos)
