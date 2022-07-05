package org.fpeterek.til.typechecking.types


class AtomicType(
    override val name: String,
    val description: String = "",
) : Type() {

    override fun toString() = name

}
