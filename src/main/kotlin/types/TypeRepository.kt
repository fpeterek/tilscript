package org.fpeterek.til.typechecking.types

import org.fpeterek.til.typechecking.greek.GreekAlphabet
import kotlin.math.abs

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

    private val counters = mapping.map { Pair(it.key, 0) }.toMap().toMutableMap()

    private val types = mutableMapOf<String, Type>()

    init {
        addType(AtomicType.Nu)
        addType(AtomicType.Iota)
        addType(AtomicType.Tau)
        addType(AtomicType.Omega)
        addType(AtomicType.Omicron)
    }

    private fun addType(type: AtomicType) {
        types[type.shortName] = type
        types[type.name] = type
    }

    private fun addAlias(alias: TypeAlias) {
        types[alias.shortName] = alias
        types[alias.name] = alias
    }

    private fun getClosestChar(char: Char) =
        mapping.minByOrNull { abs(char.code - it.key.code) }!!.key

    private fun getAndIncNumber(char: Char) =
        (counters.getOrPut(char) { 0 } + 1).apply {
            counters[char] = this
        }

    private fun assignShortName(typeName: String): String {
        val closest = getClosestChar(typeName.first().lowercaseChar())
        val num = getAndIncNumber(closest)

        return "${mapping[closest]!!}$num"
    }

    private fun storeAtomic(type: AtomicType) = AtomicType(
        shortName=assignShortName(type.name),
        name=type.name,
        description=type.description
    ).apply(::addType)

    private fun storeAlias(alias: TypeAlias) = TypeAlias(
        shortName=assignShortName(alias.name),
        name=alias.name,
        type=alias.type
    ).apply(::addAlias)

    fun process(type: AtomicType) = when {
        type.shortName.isNotBlank() -> type
        else -> storeAtomic(type)
    }

    fun process(alias: TypeAlias) = when {
        alias.shortName.isNotBlank() -> alias
        else -> storeAlias(alias)
    }

    fun process(type: Type) = when (type) {
        is Unknown, is FunctionType, is ConstructionType -> type
        is TypeAlias -> process(type)
        is AtomicType -> process(type)
    }

    operator fun get(name: String) = types[name]
    operator fun contains(name: String) = name in types

}
