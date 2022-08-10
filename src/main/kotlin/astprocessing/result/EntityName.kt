package org.fpeterek.til.interpreter.astprocessing.result

import org.fpeterek.til.interpreter.util.SrcPosition

class EntityName(val name: String, srcPos: SrcPosition) : IntermediateResult(srcPos) {

    constructor(symbol: Symbol) : this(symbol.symbol, symbol.position)

}
