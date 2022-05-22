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
        level = value.depth,
        offset = value.leftOffset,
        children = listOf(),
    ).apply { add(this) }

    private fun traverseComposite(composite: Composite) = TextBlob(
        text = composite.typename,
        level = composite.depth,
        offset = composite.leftOffset,
        children = listOf(traverse(composite.treeData))
    ).apply { add(this) }

    private fun traverseComposition(composition: TilComposition) = TextBlob(
        text = composition.typename,
        level = composition.depth,
        offset = composition.leftOffset,
        children = composition.args.map(::traverse)
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