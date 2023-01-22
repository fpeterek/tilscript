package org.fpeterek.tilscript.common.types

object Unknown : Type() {

    override val name get() = "Unknown"

    override fun equals(other: Any?) = other != null && other is Type
}
