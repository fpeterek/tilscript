package org.fpeterek.til.typechecking.constructions

class Closure(
    val variables: List<Variable>,
    val construction: Construction,
) : Construction(construction.constructionType) {

}
