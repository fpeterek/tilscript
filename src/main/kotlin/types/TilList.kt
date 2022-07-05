package org.fpeterek.til.typechecking.types

class TilList(val type: Type) : Type() {

    override val name
        get() = "List($type)"

    override fun toString() = name
}
