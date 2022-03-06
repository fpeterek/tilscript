package org.fpeterek.til.typechecking.constructions

import org.fpeterek.til.typechecking.types.ConstructionType

class Composition(
    val function: TilFunction,
    val args: List<Construction>
) : Construction(ConstructionType(args.maxOfOrNull { it.constructionType.order } ?: 1))
