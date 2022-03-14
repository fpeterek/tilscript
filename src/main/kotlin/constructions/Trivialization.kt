package org.fpeterek.til.typechecking.constructions

import org.fpeterek.til.typechecking.types.Type

class Trivialization(val construction: Construction, constructionType: Type) : Construction(constructionType) {

    override fun toString() = "'$construction"

}