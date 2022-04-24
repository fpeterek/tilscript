package org.fpeterek.til.typechecking.astprocessing.result

import org.fpeterek.til.typechecking.util.SrcPosition

class TypedVar(
    val name: String,
    val type: String,
    srcPos: SrcPosition
) : IntermediateResult(srcPos) {

    constructor(name: VarName, type: TypeName, srcPos: SrcPosition) : this(name.name, type.name, srcPos)

}
