package org.fpeterek.tilscript.common.types

object EmptyListType : Type() {
    override val name: String
        get() = "EmptyList"

    override fun toString() = name
}