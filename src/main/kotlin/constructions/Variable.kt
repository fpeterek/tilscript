package org.fpeterek.til.typechecking.constructions

import org.fpeterek.til.typechecking.types.ConstructionType
import org.fpeterek.til.typechecking.types.Type

class Variable(val name: String, val type: Type) : Construction(ConstructionType(1)) {

}
