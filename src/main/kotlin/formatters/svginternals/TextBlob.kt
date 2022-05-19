package org.fpeterek.til.typechecking.formatters.svginternals

data class TextBlob(
    val text: String,
    val offset: Int
) {

    val length get() = text.length

    override fun toString() = text
}
