package org.fpeterek.til.typechecking.constructions

import org.fpeterek.til.typechecking.types.Type

class Execution(
    val construction: Construction,
    val executionOrder: Int,
    constructionType: Type
) : Construction(constructionType) {

    init {
        if (executionOrder < 1) {
            throw RuntimeException("Execution order must be 1 or greater")
        }
    }

    override fun toString() = "$executionOrder^$construction"

}
