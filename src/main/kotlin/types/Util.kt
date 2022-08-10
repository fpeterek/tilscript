package org.fpeterek.til.typechecking.types

import org.fpeterek.til.typechecking.sentence.*
import org.fpeterek.til.typechecking.interpreter.builtins.Types
import org.fpeterek.til.typechecking.util.SrcPosition

object Util {

    val w = Variable("w", SrcPosition(-1, -1), Types.World)
    val t = Variable("t", SrcPosition(-1, -1), Types.Time)

    fun FunctionType.intensionalize() =
        FunctionType(FunctionType(this, Types.Time), Types.World)

    fun AtomicType.intensionalize() =
        FunctionType(FunctionType(this, Types.Time), Types.World)

    fun Closure.intensionalize() =
        Closure(listOf(w), Closure(listOf(t), this, srcPos=position), srcPos=position)

    fun <T : Construction> T.trivialize() =
        Trivialization(
            construction=this,
            constructedType=constructionType,
            constructionType=ConstructionType,
            srcPos=position
        )

    fun Composition.extensionalize(w: Construction, t: Construction) =
        this.compose(w).compose(t)

    fun TilFunction.extensionalize(w: Construction, t: Construction) =
        this.trivialize().compose(w).compose(t)

    fun Construction.compose(vararg args: Construction) = when (this) {
        is TilFunction -> Composition(this.trivialize(), args.toList(), position)
        else -> Composition(this, args.toList(), position)
    }
}
