package org.fpeterek.til.interpreter.astprocessing.result

import org.fpeterek.til.interpreter.util.SrcPosition

class Symbol(val symbol: String, srcPos: SrcPosition) : IntermediateResult(srcPos)
