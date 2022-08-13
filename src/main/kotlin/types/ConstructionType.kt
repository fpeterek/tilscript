package org.fpeterek.tilscript.interpreter.types

object ConstructionType : Type() {

    override val name get() = "Construction"

    override fun equals(other: Any?) = other != null && other is ConstructionType

    override fun toString() = name
}
