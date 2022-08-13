package org.fpeterek.tilscript.interpreter.astprocessing.result

import org.fpeterek.tilscript.interpreter.util.SrcPosition

class Sentences(val sentences: List<IntermediateResult>, srcPos: SrcPosition) : IntermediateResult(srcPos)
