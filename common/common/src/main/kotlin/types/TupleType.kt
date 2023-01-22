package org.fpeterek.tilscript.common.types

class TupleType(val types: List<Type>) : Type() {

    constructor(vararg types: Type) : this(types.toList())

    override val name
        get() = "Tuple<${types.joinToString(separator = ", ")}>"
}
