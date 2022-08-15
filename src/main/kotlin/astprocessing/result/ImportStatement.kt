package org.fpeterek.tilscript.interpreter.astprocessing.result

import org.fpeterek.tilscript.interpreter.util.SrcPosition

class ImportStatement(val file: String, srcPos: SrcPosition) : IntermediateResult(srcPos)
