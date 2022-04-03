package org.fpeterek.til.typechecking.astprocessing.result

class TypedVar(
    val name: String,
    val type: String,
) : IntermediateResult() {

    constructor(name: VarName, type: TypeName) : this(name.name, type.name)

}
