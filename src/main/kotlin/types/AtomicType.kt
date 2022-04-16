package org.fpeterek.til.typechecking.types

import org.fpeterek.til.typechecking.greek.GreekAlphabet

class AtomicType(
    val shortName: String,
    val name: String,
    val description: String = "",
) : Type() {

    companion object {
        val Omicron = AtomicType(GreekAlphabet.omicron, "Bool", "Truth values")
        val Iota = AtomicType(GreekAlphabet.iota, "Indiv", "Individuals")
        val Tau = AtomicType(GreekAlphabet.tau, "Time", "Timestamps")
        val Omega = AtomicType(GreekAlphabet.omega, "World", "Worlds")
        val Eta = AtomicType(GreekAlphabet.eta, "Real", "Real numbers")
        val Nu = AtomicType(GreekAlphabet.nu, "Int", "Whole numbers")

        val defaultTypes = listOf(
            Omicron, Iota, Tau, Omega, Eta, Nu
        )
    }

    override fun equals(other: Any?) = other != null && other is AtomicType &&
            shortName == other.shortName

    override fun hashCode() = shortName.hashCode()

    override fun toString() = shortName

}
