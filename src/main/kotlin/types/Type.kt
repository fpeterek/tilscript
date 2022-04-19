package org.fpeterek.til.typechecking.types

sealed class Type {
    abstract val name: String
    abstract val shortName: String
}
