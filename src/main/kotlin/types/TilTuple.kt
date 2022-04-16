package org.fpeterek.til.typechecking.types

class TilTuple(val type: Type) : Type() {
    val name
        get() = "Tuple($type)"

    val shortName
        get() = "<$type>"

    override fun toString() = shortName
}
