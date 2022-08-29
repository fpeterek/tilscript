package org.fpeterek.tilscript.interpreter.types


class AtomicType(
    override val name: String,
) : Type() {

    override fun toString() = name

}
