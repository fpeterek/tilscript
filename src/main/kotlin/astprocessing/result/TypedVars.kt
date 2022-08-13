package org.fpeterek.tilscript.interpreter.astprocessing.result

import org.fpeterek.tilscript.interpreter.util.SrcPosition

class TypedVars(val vars: List<TypedVar>, srcPos: SrcPosition) : IntermediateResult(srcPos)
