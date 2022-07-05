package org.fpeterek.til.typechecking.types

object Unknown : Type() {

    override val name get() = "Unknown"

    override fun equals(other: Any?) = other != null && other is Type

    override fun toString() = name
}
