package org.fpeterek.til.typechecking.astprocessing.result

sealed class DataType : IntermediateResult() {

    fun intensionalize() = ClassType(listOf(
        ClassType(listOf(
            this,
            TypeName("Time")
        )),
        TypeName("World")
    ))

    sealed class Collection(val type: DataType) : DataType() {
        class Tuple(type: DataType) : Collection(type)
        class List(type: DataType) : Collection(type)
    }

    class PrimitiveType(val typeName: TypeName) : DataType()
    class ClassType(val signature: List<IntermediateResult>) : DataType()
}
