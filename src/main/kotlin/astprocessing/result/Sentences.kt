package org.fpeterek.til.interpreter.astprocessing.result

import org.fpeterek.til.interpreter.util.SrcPosition

class Sentences(val sentences: List<IntermediateResult>, srcPos: SrcPosition) : IntermediateResult(srcPos)
