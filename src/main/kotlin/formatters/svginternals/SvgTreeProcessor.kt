package org.fpeterek.til.typechecking.formatters.svginternals

import org.fpeterek.til.typechecking.sentence.*

class SvgTreeProcessor private constructor(
    private val construction: Construction,
    private val tree: SentencePart,
){
    companion object {
        fun process(construction: Construction, tree: SentencePart): List<List<TextBlob>> =
            SvgTreeProcessor(construction, tree).process()
    }

    private val levels = mutableListOf<MutableList<TextBlob>>()

    private fun add(typeInfo: TextBlob) {
        (levels.lastIndex .. typeInfo.level).forEach { _ -> levels.add(mutableListOf()) }

        levels[typeInfo.level].add(typeInfo)
    }

    private fun traverseValue(value: Value) = TextBlob(
        text = value.typename,
        level = value.depth+1, // The resulting type must be one level below whatever constructed it
        offset = value.leftOffset,
        children = listOf(TextBlob("", value.leftOffset, 0, listOf())),
    ).apply { add(this) }

    private fun traverseComposite(composite: Composite) = TextBlob(
        text = composite.typename,
        level = composite.depth+1,
        offset = composite.leftOffset,
        children = listOf(TextBlob("", composite.leftOffset, 0, listOf()), traverse(composite.treeData))
    ).apply { add(this) }

    private fun traverseComposition(composition: TilComposition) = TextBlob(
        text = composition.typename,
        level = composition.depth+1,
        offset = composition.leftOffset,
        children = listOf(TextBlob("", composition.leftOffset, 0, listOf())) + composition.args.map(::traverse)
    ).apply { add(this) }

    private fun traverse(tree: SentencePart): TextBlob = when (tree) {
        is Composite -> traverseComposite(tree)
        is TilComposition -> traverseComposition(tree)
        is Value -> traverseValue(tree)
    }

    private fun process(): List<List<TextBlob>> {
        traverse(tree)
        return listOf(TreeFlattener.flatten(tree)) + levels.filter { it.isNotEmpty() }
    }
}