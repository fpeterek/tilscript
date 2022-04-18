package org.fpeterek.til.typechecking.types

import org.fpeterek.til.typechecking.sentence.*

object Util {

    val w = Variable("w", AtomicType.Omega)
    val t = Variable("t", AtomicType.Tau)

    fun FunctionType.intensionalize() =
        FunctionType(FunctionType(this, AtomicType.Tau), AtomicType.Omega)

    fun AtomicType.intensionalize() =
        FunctionType(FunctionType(this, AtomicType.Tau), AtomicType.Omega)

    fun Closure.intensionalize() =
        Closure(listOf(w), Closure(listOf(t), this))

    fun <T : Construction> T.trivialize() =
        Trivialization(
            construction=this,
            constructedType=this.constructionType,
            constructionType=ConstructionType
        )

    fun Composition.extensionalize(w: Construction, t: Construction) =
        this.compose(w).compose(t)

    fun TilFunction.extensionalize(w: Construction, t: Construction) =
        this.trivialize().compose(w).compose(t)

    fun Construction.compose(vararg args: Construction) = when (this) {
        is TilFunction -> Composition(this.trivialize(), args.toList())
        else -> Composition(this, args.toList())
    }
}
