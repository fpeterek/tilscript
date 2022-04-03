package org.fpeterek.til.typechecking.sentence

import org.fpeterek.til.typechecking.types.Type

class TypeDefinition(
    val alias: String,
    val type: Type,
) : Definition()
