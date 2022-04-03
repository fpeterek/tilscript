package org.fpeterek.til.typechecking.astprocessing.result

class VarName(val name: String) : IntermediateResult() {

    constructor(symbol: Symbol) : this(symbol.symbol)

}
