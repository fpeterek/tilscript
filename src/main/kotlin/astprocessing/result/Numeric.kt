package org.fpeterek.til.typechecking.astprocessing.result

import org.fpeterek.til.typechecking.util.SrcPosition

class Numeric(val value: String, srcPos: SrcPosition) : IntermediateResult(srcPos)
