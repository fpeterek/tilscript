package org.fpeterek.tilscript.interpreter.astprocessing.result

import org.fpeterek.tilscript.common.SrcPosition
import kotlin.collections.List as KtList

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

    sealed class Collection(srcPos: SrcPosition) : DataType(srcPos) {
        class Tuple(val types: KtList<DataType>, srcPos: SrcPosition) : Collection(srcPos)
        class List(val type: DataType, srcPos: SrcPosition) : Collection(srcPos)
    }

    class PrimitiveType(val typeName: TypeName, srcPos: SrcPosition) : DataType(srcPos) {
        val name get() = typeName.name
    }

    class ClassType(val signature: KtList<DataType>, srcPos: SrcPosition) : DataType(srcPos)
}
