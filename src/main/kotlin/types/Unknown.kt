package org.fpeterek.til.typechecking.types

object Unknown : Type() {
    override fun equals(other: Any?) = other != null && other is Type
}
