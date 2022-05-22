package org.fpeterek.til.typechecking.formatters.svginternals

data class TextBlob(
    val text: String,
    val offset: Int,
    val level: Int,
    val children: List<TextBlob>,
) {

    val length get() = text.length

    override fun toString() = text
}
