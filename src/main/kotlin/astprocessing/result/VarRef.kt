package org.fpeterek.til.typechecking.astprocessing.result

class VarRef(val name: String) : IntermediateResult() {

    constructor(varName: VarName) : this(varName.name)

}
