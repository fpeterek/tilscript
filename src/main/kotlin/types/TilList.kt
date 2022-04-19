package org.fpeterek.til.typechecking.types

class TilList(val type: Type) : Type() {
    override val name
        get() = "List($type)"

    override val shortName
        get() = "[$type]"

    override fun toString() = shortName
}
