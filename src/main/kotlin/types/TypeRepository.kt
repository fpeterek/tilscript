package org.fpeterek.til.typechecking.types

import org.fpeterek.til.typechecking.astprocessing.result.TypeAlias
import org.fpeterek.til.typechecking.greek.GreekAlphabet

class TypeRepository {

    companion object {
        private val mapping = mapOf(
            'b' to GreekAlphabet.beta,
            'g' to GreekAlphabet.gamma,
            'd' to GreekAlphabet.delta,
            'e' to GreekAlphabet.epsilon,
            'z' to GreekAlphabet.zeta,
            'h' to GreekAlphabet.eta,
            't' to GreekAlphabet.theta,
            'k' to GreekAlphabet.kappa,
            'l' to GreekAlphabet.lambda,
            'm' to GreekAlphabet.mu,
            'x' to GreekAlphabet.xi,
            'p' to GreekAlphabet.pi,
            'r' to GreekAlphabet.rho,
            's' to GreekAlphabet.sigma,
            'y' to GreekAlphabet.upsilon,
            'f' to GreekAlphabet.phi,
            'c' to GreekAlphabet.chi,
            'i' to GreekAlphabet.psi,
        )
    }

    private val types = mutableMapOf<String, Type>()

    private val unavailable = mutableSetOf(GreekAlphabet.alpha)
    private val available
        get() = mapping.keys - unavailable

    init {
        add(AtomicType.Nu)
        add(AtomicType.Iota)
        add(AtomicType.Tau)
        add(AtomicType.Omega)
        add(AtomicType.Omicron)
    }

    fun add(type: AtomicType) {
        if (type.shortName.isNotBlank()) {
            types[type.shortName] = type
        }

        if (type.name.isNotBlank()) {
            types[type.name] = type
        }

        unavailable.add(type.shortName)
    }

    fun add(alias: TypeAlias) {
        // TODO
    }

    operator fun get(name: String) = types[name]
    operator fun contains(name: String) = name in types

}
