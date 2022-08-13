package org.fpeterek.tilscript.interpreter.astprocessing.result

import org.fpeterek.tilscript.interpreter.util.SrcPosition

class TypeAlias(val name: String, val type: DataType, srcPos: SrcPosition) : IntermediateResult(srcPos) {
    constructor(name: TypeName, type: DataType, srcPos: SrcPosition) : this(name.name, type, srcPos)
}
