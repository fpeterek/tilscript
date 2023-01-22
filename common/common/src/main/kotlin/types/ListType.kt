package org.fpeterek.tilscript.common.types

class ListType(val type: Type) : Type() {
    override val name
        get() = "List<$type>"
}
