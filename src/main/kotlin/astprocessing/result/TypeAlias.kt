package org.fpeterek.til.typechecking.astprocessing.result

class TypeAlias(val name: String, val type: DataType) : IntermediateResult() {
    constructor(name: TypeName, type: DataType) : this(name.name, type)
}
