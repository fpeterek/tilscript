package org.fpeterek.til.interpreter.astprocessing.result

import org.fpeterek.til.interpreter.util.SrcPosition

class TypedVars(val vars: List<TypedVar>, srcPos: SrcPosition) : IntermediateResult(srcPos)
