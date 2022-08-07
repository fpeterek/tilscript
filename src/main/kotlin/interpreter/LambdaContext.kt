package org.fpeterek.til.typechecking.interpreter

data class LambdaContext(
//    val captureList: List<Variable>
    val frame: StackFrame
)
