package org.fpeterek.til.interpreter.types

class ListType(val type: Type) : Type() {

    override val name
        get() = "List<$type>"

    override fun toString() = name
}
