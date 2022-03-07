package org.fpeterek.til.typechecking.constructions

class Trivialization(val construction: Construction) : Construction(construction.constructionType) {

    override fun toString() = "'$construction"

}