package org.fpeterek.til.typechecking.astprocessing.result

import org.fpeterek.til.typechecking.util.SrcPosition

sealed class DataType(srcPos: SrcPosition) : IntermediateResult(srcPos) {

    fun intensionalize(srcPos: SrcPosition) = ClassType(
        listOf(
            ClassType(
                listOf(
                    this,
                    PrimitiveType(TypeName(Symbol("Time", srcPos)), srcPos)
                ),
                srcPos
            ),
            PrimitiveType(TypeName(Symbol("World", srcPos)), srcPos)
        ),
        srcPos
    )

    sealed class Collection(val type: DataType, srcPos: SrcPosition) : DataType(srcPos) {
        class Tuple(type: DataType, srcPos: SrcPosition) : Collection(type, srcPos)
        class List(type: DataType, srcPos: SrcPosition) : Collection(type, srcPos)
    }

    class PrimitiveType(val typeName: TypeName, srcPos: SrcPosition) : DataType(srcPos) {
        val name get() = typeName.name
    }

    class ClassType(val signature: List<DataType>, srcPos: SrcPosition) : DataType(srcPos)
}
