package org.fpeterek.tilscript.interpreter.astprocessing.result

import org.fpeterek.tilscript.common.SrcPosition

class Numeric(val value: String, srcPos: SrcPosition) : IntermediateResult(srcPos)
