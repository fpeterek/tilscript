package org.fpeterek.til.typechecking.sentence

import org.fpeterek.til.typechecking.types.Type
import org.fpeterek.til.typechecking.types.TypeAlias


sealed class Definition : Sentence()

class LiteralDefinition(
    val names: List<String>,
    val type: Type,
) : Definition() {

    override fun toString() = "${names.joinToString(separator=", ")}/$type"

}

class TypeDefinition(
    val alias: TypeAlias
) : Definition() {

    override fun toString() = "${alias.shortName} := ${alias.type}"

}

class VariableDefinition(
    val variables: List<String>,
    val type: Type,
) : Definition() {

    override fun toString() = "${variables.joinToString(separator=", ")} -> $type"

}
