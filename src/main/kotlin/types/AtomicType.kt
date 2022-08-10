package org.fpeterek.til.interpreter.types


class AtomicType(
    override val name: String,
    val description: String = "",
) : Type() {

    override fun toString() = name

}
