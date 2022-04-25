package org.fpeterek.til.typechecking.types

import org.fpeterek.til.typechecking.exceptions.InvalidFunctionSignature

class FunctionType(
    val imageType: Type,
    val argTypes: List<Type>,
) : Type() {

    companion object {

        operator fun invoke(vararg signature: Type) = this(signature.toList())

        operator fun invoke(signature: List<Type>) = when {
            signature.isEmpty() ->
                throw InvalidFunctionSignature("Function signature must contain at least image type")
            else -> FunctionType(
                imageType=signature[0],
                argTypes=signature.drop(1)
            )
        }
    }

    override val name
        get() = "(${imageType.name}${argTypes.joinToString(separator="") { it.name }})"
                
    override val shortName
        get() = "(${imageType.shortName}${argTypes.joinToString(separator="") { it.shortName }})"

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

    override fun toString() = shortName
}
