package org.fpeterek.til.typechecking.astprocessing.result

sealed class DataType : IntermediateResult() {

    fun intensionalize() = ClassType(listOf(
        ClassType(listOf(
            this,
            PrimitiveType(TypeName("Time"))
        )),
        PrimitiveType(TypeName("World"))
    ))

    sealed class Collection(val type: DataType) : DataType() {
        class Tuple(type: DataType) : Collection(type)
        class List(type: DataType) : Collection(type)
    }

    class PrimitiveType(val typeName: TypeName) : DataType() {
        val name get() = typeName.name
    }

    class ClassType(val signature: List<DataType>) : DataType()
}
