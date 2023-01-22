package org.fpeterek.tilscript.common.types

object ConstructionType : Type() {

    override val name get() = "Construction"

    override fun equals(other: Any?) = other != null && other is ConstructionType
}
