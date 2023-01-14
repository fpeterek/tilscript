package org.fpeterek.tilscript.common.types

import org.fpeterek.tilscript.common.sentence.*
import org.fpeterek.tilscript.common.SrcPosition

object Util {

    val w = Variable("w", SrcPosition(-1, -1), Primitives.World)
    val t = Variable("t", SrcPosition(-1, -1), Primitives.Time)

    fun FunctionType.intensionalize() =
        FunctionType(FunctionType(this, Primitives.Time), Primitives.World)

    fun AtomicType.intensionalize() =
        FunctionType(FunctionType(this, Primitives.Time), Primitives.World)

    fun Closure.intensionalize() =
        Closure(
            listOf(w),
            Closure(
                listOf(t),
                this,
                srcPos=position,
                returnType = FunctionType(this.functionType, Primitives.Time)
            ),
            srcPos=position,
            returnType = FunctionType(FunctionType(this.functionType, Primitives.Time), Primitives.World)
        )

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

    val AtomicType.isGeneric       get() = false
    val ConstructionType.isGeneric get() = false
    val FunctionType.isGeneric     get() = imageType.isGeneric || argTypes.any { it.isGeneric }
    val GenericType.isGeneric      get() = true
    val ListType.isGeneric         get() = type.isGeneric
    val TupleType.isGeneric        get() = types.any { it.isGeneric }
    val TypeAlias.isGeneric        get() = type.isGeneric
    val Unknown.isGeneric          get() = false
    val EmptyListType.isGeneric    get() = false

    val Type.isGeneric: Boolean
        get() = when (this) {
            is AtomicType       -> isGeneric
            is ConstructionType -> isGeneric
            is FunctionType     -> isGeneric
            is GenericType      -> isGeneric
            is ListType         -> isGeneric
            is EmptyListType    -> isGeneric
            is TupleType        -> isGeneric
            is TypeAlias        -> isGeneric
            is Unknown          -> isGeneric
        }
}
