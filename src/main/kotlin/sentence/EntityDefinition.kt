package org.fpeterek.til.typechecking.sentence

import org.fpeterek.til.typechecking.types.Type

class EntityDefinition(
    val entities: List<String>,
    val type: Type,
) : Definition()
