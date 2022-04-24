package org.fpeterek.til.typechecking.astprocessing.result

import org.fpeterek.til.typechecking.util.SrcPosition

class TypeAlias(val name: String, val type: DataType, srcPos: SrcPosition) : IntermediateResult(srcPos) {
    constructor(name: TypeName, type: DataType, srcPos: SrcPosition) : this(name.name, type, srcPos)
}
