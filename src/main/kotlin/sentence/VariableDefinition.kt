package org.fpeterek.til.typechecking.sentence

import org.fpeterek.til.typechecking.types.Type

class VariableDefinition(
    val variables: List<String>,
    val type: Type,
) : Definition()
