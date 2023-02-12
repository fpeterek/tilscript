package org.fpeterek.tilscript.math

import org.fpeterek.tilscript.common.interpreterinterface.SymbolRegistrar
import org.fpeterek.tilscript.common.sentence.TilFunction
import org.fpeterek.tilscript.common.types.StructType
import org.fpeterek.tilscript.common.types.TypeAlias


class Registrar : SymbolRegistrar {

    override val functions get() = listOf(
        Sin,
        Cos,
        Tan,
        Ln,
        Log,
        Round
    )

    override val aliases get()  = emptyList<TypeAlias>()
    override val symbols get() = listOf(Pi, E)
    override val functionDeclarations get() = listOf<TilFunction>()
    override val structs get() = emptyList<StructType>()
}
