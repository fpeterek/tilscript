package org.fpeterek.til.typechecking.sentence

import org.fpeterek.til.typechecking.types.Type
import org.fpeterek.til.typechecking.types.TypeAlias


sealed class Definition : Sentence()

class LiteralDefinition(val literals: List<Literal>) : Definition() {

    val type
        get() = literals.first().constructedType
    val names
        get() = literals.asSequence().map { it.value }

    constructor(names: List<String>, type: Type) : this(names.map { Literal(it, type) })

    override fun toString() = "${names.joinToString(separator=", ")}/$type"
}

class TypeDefinition(val alias: TypeAlias) : Definition() {
    override fun toString() = "${alias.shortName} := ${alias.type}"
}

class VariableDefinition(val variables: List<Variable>) : Definition() {

    val type
        get() = variables.first().constructedType
    val names
        get() = variables.asSequence().map { it.name }

    constructor(variables: List<String>, type: Type) : this(variables.map { Variable(it, type) })

    override fun toString() = "${names.joinToString(separator=", ")} -> $type"
}
