package org.fpeterek.til.typechecking.constructions

class Execution(
    private val construction: Construction,
    private val order: Int,
) : Construction(construction.constructionType) {

    override fun toString() = "$order^$construction"

}
