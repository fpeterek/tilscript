package org.fpeterek.tilscript.interpreter.types

class TupleType(val types: List<Type>) : Type() {

    constructor(vararg types: Type) : this(types.toList())

    override val name
        get() = "Tuple<${types.joinToString(separator = ", ")}>"

    override fun toString() = name
}
