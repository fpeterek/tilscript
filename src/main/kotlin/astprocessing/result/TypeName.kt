package org.fpeterek.til.typechecking.astprocessing.result

class TypeName(val name: String) : IntermediateResult() {

    constructor(symbol: Symbol) : this(symbol.symbol)

}
