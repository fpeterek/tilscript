package org.fpeterek.til.typechecking.types

object ConstructionType : Type() {

    override val name get() = "*"
    override val shortName get() = "*"

    override fun equals(other: Any?) = other != null && other is ConstructionType

    override fun toString() = name
}
