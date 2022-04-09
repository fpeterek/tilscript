package org.fpeterek.til.typechecking.sentence

import org.fpeterek.til.typechecking.types.Type


sealed class Definition : Sentence()

class LiteralDefinition(
    val names: List<String>,
    val type: Type,
) : Definition()

class TypeDefinition(
    val alias: String,
    val type: Type,
) : Definition()

class VariableDefinition(
    val variables: List<String>,
    val type: Type,
) : Definition()
