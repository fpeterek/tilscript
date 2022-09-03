package org.fpeterek.tilscript.interpreter.astprocessing.result

import org.fpeterek.tilscript.common.SrcPosition

class ImportStatement(val file: String, srcPos: SrcPosition) : IntermediateResult(srcPos)
