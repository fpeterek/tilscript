package org.fpeterek.til.typechecking.types

class FunctionType(
    private val imageType: Type,
    private val argTypes: List<Type>,
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

    override val order
        get() = argTypes.maxOfOrNull { it.order } ?: 1

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

    val nextArg: Type = when {
        isConstant -> throw RuntimeException("Constant functions accept no arguments")
        else -> argTypes.last()
    }

    override fun matches(other: Type) = when (other) {
        is AtomicType -> isConstant && imageType.matches(other)
        is FunctionType -> imageType.matches(other.imageType) &&
                argTypes.zip(other.argTypes).all { (t1, t2) -> t1.matches(t2) }
        is ConstructionType -> false
    }

    fun apply(arg: Type) = when {
        isConstant -> throw RuntimeException("Cannot apply argument to constant function")
        arg.matches(nextArg) -> FunctionType(imageType, argTypes.dropLast(1))
        else -> throw RuntimeException("Type mismatch")
    }

    fun apply() = when {
        !isConstant -> throw RuntimeException("Function is not constant")
        else -> imageType
    }

    override fun toString() = "(" + imageType + argTypes.joinToString() + ")"
}
