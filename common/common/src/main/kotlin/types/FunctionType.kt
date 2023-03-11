package org.fpeterek.tilscript.common.types

import org.fpeterek.tilscript.common.die

class FunctionType(
    val imageType: Type,
    val argTypes: List<Type>,
) : Type() {

    init {
        if (argTypes.isEmpty()) {
            throw RuntimeException("Function arity must be at least one.")
        }
    }

    companion object {

        operator fun invoke(vararg signature: Type) = this(signature.toList())

        operator fun invoke(signature: List<Type>) = when {
            signature.isEmpty() ->
                die("Function signature must contain at least image type")
            else -> FunctionType(
                imageType=signature[0],
                argTypes=signature.drop(1)
            )
        }
    }

    override val name
        get() = "(${imageType.name} ${argTypes.joinToString(separator=" ")})"
                
    private val Type.isKnown
        get() = when (this) {
            is FunctionType -> this.fullyTyped
            else -> this !is Unknown
        }

    val signature: List<Type>
        get() = listOf(imageType) + argTypes

    private val imgTypeIsKnown
        get() = imageType.isKnown

    private val argTypesAreKnown
        get() = argTypes.all { it.isKnown }

    val fullyTyped: Boolean
        get() = imgTypeIsKnown && argTypesAreKnown

    val arity
        get() = argTypes.size
}
