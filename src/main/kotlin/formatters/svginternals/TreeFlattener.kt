package org.fpeterek.til.typechecking.formatters.svginternals


object TreeFlattener {

    private fun flattenString(str: String, offset: Int) = when {
        str.isNotBlank() -> listOf(TextBlob(str, offset))
        else             -> listOf()
    }

    private fun flatten(comp: Composite): List<TextBlob> {

        val prefix = flattenString(comp.prefix, comp.leftOffset)
        val data = prefix + flatten(comp.treeData)
        val suffixOffset = data.sumOf { it.length }

        return data + flattenString(comp.suffix, suffixOffset)
    }

    private fun flatten(comp: TilComposition): List<TextBlob> {

        val prefix = flattenString(comp.prefix, comp.leftOffset)

        val args = comp.args.flatMapIndexed { index: Int, sentencePart: SentencePart ->
            val space = when (index) {
                0 -> listOf()
                else -> listOf(TextBlob(" ", sentencePart.leftOffset - 1))
            }

            space + flatten(sentencePart)
        }

        val data = prefix + args
        val suffixOffset = data.sumOf { it.length }

        return data + flattenString(comp.suffix, suffixOffset)
    }

    private fun flatten(value: Value) = listOf(TextBlob(value.toString(), value.leftOffset))

    fun flatten(tree: SentencePart): List<TextBlob> = when (tree) {
        is Composite      -> flatten(tree)
        is TilComposition -> flatten(tree)
        is Value          -> flatten(tree)
    }

}