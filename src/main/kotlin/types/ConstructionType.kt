package org.fpeterek.til.typechecking.types

class ConstructionType(override val order: Int) : Type() {
    init {
        if (order < 1) {
            throw RuntimeException("Invalid order")
        }
    }

    override fun matches(other: Type) = when (other) {
        is ConstructionType -> order == other.order
        else -> false
    }

    override fun toString() = "*$order"
}
