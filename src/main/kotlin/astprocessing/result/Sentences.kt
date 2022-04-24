package org.fpeterek.til.typechecking.astprocessing.result

import org.fpeterek.til.typechecking.util.SrcPosition

class Sentences(val sentences: List<IntermediateResult>, srcPos: SrcPosition) : IntermediateResult(srcPos)
