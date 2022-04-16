package org.fpeterek.til.typechecking.types

class TilList(val type: Type) : Type() {
    val name
        get() = "List($type)"

    val shortName
        get() = "[$type]"

    override fun toString() = shortName
}
