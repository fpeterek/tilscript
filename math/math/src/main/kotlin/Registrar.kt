package org.fpeterek.tilscript.math

import org.fpeterek.tilscript.common.interpreterinterface.SymbolRegistrar
import org.fpeterek.tilscript.common.sentence.TilFunction
import org.fpeterek.tilscript.common.types.Type


class Registrar : SymbolRegistrar {

    override val functions get() = listOf(
        Sin,
        Cos,
        Tan,
        Ln,
        Log,
        Round
    )

    override val types get()  = emptyList<Type>()
    override val values get() = listOf(Pi, E)
    override val functionDeclarations get() = listOf<TilFunction>()
}
