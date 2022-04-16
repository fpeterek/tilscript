package org.fpeterek.til.typechecking.types

class FunctionType(
    val imageType: Type,
    val argTypes: List<Type>,
) : Type() {

    companion object {

        operator fun invoke(vararg signature: Type) = this(signature.toList())

        operator fun invoke(signature: List<Type>) = when {
            signature.isEmpty() ->
                throw RuntimeException("Function signature must contain at least image type")
            else -> FunctionType(
                imageType=signature[0],
                argTypes=signature.drop(1)
            )
        }
    }

    private val Type.isKnown
        get() = when (this) {
            is FunctionType -> this.fullyTyped
            else -> this !is Unknown
        }

    private val imgTypeIsKnown
        get() = imageType.isKnown

    private val argTypesAreKnown
        get() = argTypes.all { it.isKnown }

    val fullyTyped: Boolean
        get() = imgTypeIsKnown && argTypesAreKnown

    val arity
        get() = argTypes.size

    val isConstant
        get() = arity == 0
    val isUnary
        get() = arity == 1
    val isBinary
        get() = arity == 2
    val isTernary
        get() = arity == 3

    val nextArg: Type
        get() = when {
            isConstant -> throw RuntimeException("Constant functions accept no arguments")
            else -> argTypes.last()
        }

    /*fun apply(arg: Type) = when {
        isConstant -> throw RuntimeException("Cannot apply argument to constant function")
        // arg.matches(nextArg) -> FunctionType(imageType, argTypes.dropLast(1))
        else -> throw RuntimeException("Type mismatch")
    }

    fun apply() = when {
        !isConstant -> throw RuntimeException("Function is not constant")
        else -> imageType
    }*/

    override fun equals(other: Any?) =
        other != null && other is FunctionType && other.imageType == this.imageType &&
                other.argTypes == this.argTypes

    override fun toString() = "($imageType${argTypes.joinToString(separator="")})"
}
