package org.fpeterek.til.interpreter.types

class TupleType(val types: List<Type>) : Type() {

    constructor(type: Type) : this(listOf(type))

    override val name
        get() = "Tuple<${types.joinToString(separator = ", ")}>"

    override fun toString() = name
}
