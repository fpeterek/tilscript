package org.fpeterek.tilscript.common.types


class AtomicType(
    override val name: String,
) : Type() {

    override fun toString() = name

}