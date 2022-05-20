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

    private val levels = mutableListOf<MutableList<TypeInfo>>()

    private fun add(typeInfo: TypeInfo) {
        val adjustedLevel = typeInfo.level - 1
        (levels.lastIndex .. adjustedLevel).forEach { _ -> levels.add(mutableListOf()) }

        levels[adjustedLevel].add(typeInfo)
    }

    private fun traverseValue(value: Value) {
        add(
            TypeInfo(
                typename = value.typename,
                level = value.depth,
                leftOffset = value.leftOffset
            )
        )
    }

    private fun traverseComposite(composite: Composite) {
        add(
            TypeInfo(
                typename = composite.typename,
                level = composite.depth,
                leftOffset = composite.leftOffset
            )
        )

        traverse(composite.treeData)
    }

    private fun traverseComposition(composition: TilComposition) {
        add(
            TypeInfo(
                typename = composition.typename,
                level = composition.depth,
                leftOffset = composition.leftOffset
            )
        )

        composition.args.forEach {
            traverse(it)
        }
    }

    private fun traverse(tree: SentencePart) = when (tree) {
        is Composite -> traverseComposite(tree)
        is TilComposition -> traverseComposition(tree)
        is Value -> traverseValue(tree)
    }

    private fun asBlobs(level: List<TypeInfo>) = level.map { TextBlob(it.typename, it.leftOffset) }

    private fun levelsAsBlobs() = levels.map(::asBlobs).filter { it.isNotEmpty() }

    private fun toBlobs(): List<List<TextBlob>> = listOf(TreeFlattener.flatten(tree)) + levelsAsBlobs()

    private fun process(): List<List<TextBlob>> {
        traverse(tree)
        return toBlobs()
    }
}