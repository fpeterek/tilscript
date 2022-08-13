package org.fpeterek.til.interpreter.types

object ConstructionType : Type() {

    override val name get() = "Construction"

    override fun equals(other: Any?) = other != null && other is ConstructionType

    override fun toString() = name
}
