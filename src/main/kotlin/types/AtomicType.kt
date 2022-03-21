package org.fpeterek.til.typechecking.types

import org.fpeterek.til.typechecking.greek.GreekAlphabet

class AtomicType private constructor(
    val shortName: String,
    val name: String,
    val description: String = "",
) : Type() {

    companion object {

        private val types = mutableMapOf<String, AtomicType>()

        private fun addType(type: AtomicType) {
            if (type.shortName in types) {
                throw RuntimeException("Type '${type.shortName}' already exists")
            }
            types[type.shortName] = type
        }

        val Omicron = AtomicType(GreekAlphabet.omicron, "Omicron", "Truth values")
        val Iota = AtomicType(GreekAlphabet.iota, "Iota", "Individuals")
        val Tau = AtomicType(GreekAlphabet.tau, "Tau", "Real numbers/timestamps")
        val Omega = AtomicType(GreekAlphabet.omega, "Omega", "Worlds")
        val Nu = AtomicType(GreekAlphabet.nu, "Nu", "Whole numbers")

        operator fun invoke(shortName: String, name: String, description: String): AtomicType =
            AtomicType(shortName, name, description).apply {
                addType(this)
            }
    }

    override fun equals(other: Any?) = other != null && other is AtomicType &&
            shortName == other.shortName

    override fun hashCode() = shortName.hashCode()

    override fun toString() = shortName

}
