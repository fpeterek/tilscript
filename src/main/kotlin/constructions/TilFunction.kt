package org.fpeterek.til.typechecking.constructions

import org.fpeterek.til.typechecking.types.ConstructionType
import org.fpeterek.til.typechecking.types.FunctionType


class TilFunction(
    val name: String,
    val type: FunctionType,
) : Construction(ConstructionType(1)) {

}
