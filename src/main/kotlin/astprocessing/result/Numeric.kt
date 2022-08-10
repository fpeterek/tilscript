package org.fpeterek.til.interpreter.astprocessing.result

import org.fpeterek.til.interpreter.util.SrcPosition

class Numeric(val value: String, srcPos: SrcPosition) : IntermediateResult(srcPos)
