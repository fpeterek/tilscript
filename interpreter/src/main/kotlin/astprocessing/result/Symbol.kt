package org.fpeterek.tilscript.interpreter.astprocessing.result

import org.fpeterek.tilscript.common.SrcPosition

class Symbol(val symbol: String, srcPos: SrcPosition) : IntermediateResult(srcPos)
