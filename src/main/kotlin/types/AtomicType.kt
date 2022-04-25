package org.fpeterek.til.typechecking.types


class AtomicType(
    override val shortName: String,
    override val name: String,
    val description: String = "",
) : Type() {

    override fun toString() = shortName

}
