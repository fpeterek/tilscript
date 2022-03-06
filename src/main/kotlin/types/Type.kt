package org.fpeterek.til.typechecking.types

sealed class Type {
    abstract fun matches(other: Type): Boolean

    open val order
        get() = 1
}
