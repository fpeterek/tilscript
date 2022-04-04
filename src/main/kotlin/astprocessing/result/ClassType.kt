package org.fpeterek.til.typechecking.astprocessing.result

class ClassType(val signature: List<IntermediateResult>) : IntermediateResult() {

    fun intensionalize() = ClassType(listOf(
        ClassType(listOf(
            this,
            TypeName("Time")
        )),
        TypeName("World")
    ))

}
