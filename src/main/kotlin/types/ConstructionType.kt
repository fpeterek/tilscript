package org.fpeterek.til.typechecking.types

object ConstructionType : Type() {

    override fun equals(other: Any?) = other != null && other is ConstructionType

    override fun toString() = "*"
}
