package org.fpeterek.til.typechecking.astprocessing.result

class Composition(val fn: IntermediateResult, val args: List<IntermediateResult>) :
        IntermediateResult()
