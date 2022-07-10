package org.fpeterek.til.typechecking.interpreter

import org.fpeterek.til.typechecking.sentence.Variable

data class LambdaContext(
    val captureList: List<Variable>
)
