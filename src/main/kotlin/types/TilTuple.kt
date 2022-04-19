package org.fpeterek.til.typechecking.types

class TilTuple(val type: Type) : Type() {
    override val name
        get() = "Tuple($type)"

    override val shortName
        get() = "<$type>"

    override fun toString() = shortName
}
