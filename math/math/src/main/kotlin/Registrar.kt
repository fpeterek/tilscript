package org.fpeterek.tilscript.math

import org.fpeterek.tilscript.common.interpreterinterface.SymbolRegistrar
import org.fpeterek.tilscript.common.sentence.TilFunction
import org.fpeterek.tilscript.common.types.StructType
import org.fpeterek.tilscript.common.types.TypeAlias
import org.fpeterek.tilscript.javamath.InvSqrt


class Registrar : SymbolRegistrar {

    override val functions
        get() = listOf(
            Sin,
            Cos,
            Tan,
            Ln,
            Log,
            Log2,
            Log10,
            Round,
            Sqrt,
            InvSqrt()
        )

    override val aliases get() = emptyList<TypeAlias>()
    override val symbols get() = listOf(Pi, E)
    override val functionDeclarations get() = listOf<TilFunction>()
    override val structs get() = emptyList<StructType>()
    override val variables get() = listOf(e, pi)
}
