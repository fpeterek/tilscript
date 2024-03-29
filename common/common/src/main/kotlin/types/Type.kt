package org.fpeterek.tilscript.common.types

sealed class Type {
    abstract val name: String

    override fun toString() = name
}
