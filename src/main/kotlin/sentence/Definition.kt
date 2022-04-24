package org.fpeterek.til.typechecking.sentence

import org.fpeterek.til.typechecking.types.Type
import org.fpeterek.til.typechecking.types.TypeAlias
import org.fpeterek.til.typechecking.util.SrcPosition


sealed class Definition(srcPos: SrcPosition) : Sentence(srcPos)

class LiteralDefinition(val literals: List<Literal>, srcPos: SrcPosition) : Definition(srcPos) {

    val type
        get() = literals.first().constructedType
    val names
        get() = literals.asSequence().map { it.value }

    override fun toString() = "${names.joinToString(separator=", ")}/$type"
}

class TypeDefinition(val alias: TypeAlias, srcPos: SrcPosition) : Definition(srcPos) {
    override fun toString() = "${alias.shortName} := ${alias.type}"
}

class VariableDefinition(val variables: List<Variable>, srcPos: SrcPosition) : Definition(srcPos) {

    val type
        get() = variables.first().constructedType
    val names
        get() = variables.asSequence().map { it.name }

    override fun toString() = "${names.joinToString(separator=", ")} -> $type"
}

class FunctionDefinition(val functions: List<TilFunction>, srcPos: SrcPosition) : Definition(srcPos) {

    val type
        get() = functions.first().constructedType

    val names
        get() = functions.asSequence().map { it.name }

    override fun toString() = "${names.joinToString(separator=", ")}/$type"

}
