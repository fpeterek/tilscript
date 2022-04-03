package org.fpeterek.til.typechecking.astprocessing.result

class EntityName(val name: String) : IntermediateResult() {

    constructor(symbol: Symbol) : this(symbol.symbol)

}
