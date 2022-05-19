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

    private var leftOffset = 0
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
                leftOffset = leftOffset
            )
        )
    }

    private fun traverseComposite(composite: Composite) {
        val oldOffset = leftOffset

        add(
            TypeInfo(
                typename = composite.typename,
                level = composite.depth,
                leftOffset = leftOffset
            )
        )

        leftOffset += composite.prefix.length

        traverse(composite.treeData)

        leftOffset = oldOffset + composite.toString().length
    }

    private fun traverseComposition(composition: TilComposition) {
        val oldOffset = leftOffset

        add(
            TypeInfo(
                typename = composition.typename,
                level = composition.depth,
                leftOffset = leftOffset
            )
        )

        leftOffset += composition.prefix.length

        composition.args.forEach {
            traverse(it)
            leftOffset += 1 // Space after each composition element
        }

        leftOffset = oldOffset + composition.toString().length
    }

    private fun traverse(tree: SentencePart) = when (tree) {
        is Composite -> traverseComposite(tree)
        is TilComposition -> traverseComposition(tree)
        is Value -> traverseValue(tree)
    }

    private fun asBlobs(level: List<TypeInfo>) = level.flatMapIndexed { idx, ti ->
        when (idx) {
            0 -> listOf(TextBlob(ti.typename, ti.leftOffset))
            else -> listOf(TextBlob("   ", ti.leftOffset), TextBlob(ti.typename, ti.leftOffset + 3))
        }
    }

    private fun levelsAsBlobs() = levels.map(::asBlobs).filter { it.isNotEmpty() }

    private fun toBlobs(): List<List<TextBlob>> = listOf(TreeFlattener.flatten(tree)) + levelsAsBlobs()

    private fun process(): List<List<TextBlob>> {
        traverse(tree)
        return toBlobs()
    }
}