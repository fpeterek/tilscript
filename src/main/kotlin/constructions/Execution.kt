package org.fpeterek.til.typechecking.constructions

import org.fpeterek.til.typechecking.types.Type

class Execution(
    private val construction: Construction,
    private val order: Int,
    constructionType: Type
) : Construction(constructionType) {

    init {
        if (order < 1) {
            throw RuntimeException("Execution order must be 1 or greater")
        }
    }

    override fun toString() = "$order^$construction"

}
