package org.fpeterek.til.typechecking.types

import org.fpeterek.til.typechecking.sentence.*
import org.fpeterek.til.typechecking.tilscript.Builtins

object Util {

    val w = Variable("w", Builtins.Omega)
    val t = Variable("t", Builtins.Tau)

    fun FunctionType.intensionalize() =
        FunctionType(FunctionType(this, Builtins.Tau), Builtins.Omega)

    fun AtomicType.intensionalize() =
        FunctionType(FunctionType(this, Builtins.Tau), Builtins.Omega)

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
