package org.fpeterek.til.typechecking.astprocessing.result

import org.fpeterek.til.typechecking.util.SrcPosition

class Symbol(val symbol: String, srcPos: SrcPosition) : IntermediateResult(srcPos)
