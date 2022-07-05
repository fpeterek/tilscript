package org.fpeterek.til.typechecking.types

class TilTuple(val type: Type) : Type() {
    override val name
        get() = "Tuple($type)"

    override fun toString() = name
}
