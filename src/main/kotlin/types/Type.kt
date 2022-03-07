package org.fpeterek.til.typechecking.types

sealed class Type {
    open val order
        get() = 1
}
