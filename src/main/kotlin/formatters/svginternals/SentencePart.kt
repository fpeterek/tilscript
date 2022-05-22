package org.fpeterek.til.typechecking.formatters.svginternals


sealed class SentencePart(
    val typename: String,
    val leftOffset: Int,
) {
    abstract val prefix: String
    abstract val data: String
    abstract val suffix: String

    abstract val depth: Int

    val length get() = toString().length

    override fun toString() = "$prefix$data$suffix"
}

class Value(
    override val data: String,
    typename: String,
    leftOffset: Int,
) : SentencePart(typename, leftOffset) {
    override val prefix get() = ""
    override val suffix get() = ""

    override val depth: Int get() = 0
}

class Composite(
    override val data: String,
    override val prefix: String,
    override val suffix: String,
    val treeData: SentencePart,
    typename: String,
    leftOffset: Int,
) : SentencePart(typename, leftOffset) {

    override val depth: Int get() = treeData.depth + 1

}

class TilComposition(
    override val data: String,
    val args: List<SentencePart>,
    typename: String,
    leftOffset: Int,
) : SentencePart(typename, leftOffset) {
    override val prefix = "["
    override val suffix = "]"

    override val depth: Int
        get() = args.maxOf { it.depth } + 1

}
