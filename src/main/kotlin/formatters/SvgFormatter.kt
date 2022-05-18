package org.fpeterek.til.typechecking.formatters

import org.fpeterek.til.typechecking.formatters.svginternals.*
import org.fpeterek.til.typechecking.sentence.*


class SvgFormatter private constructor(private val construction: Construction) {

    companion object {
        fun format(constructions: List<Construction>): String = SvgFormatter(constructions.first()).formSvg()
    }

    private val constructionString = construction.toString()
    private val tree = SvgTreeCreator.createTree(construction)

    private val charWidth = 5
    private val levelSize = 50

    private val builder = StringBuilder()
        .append("""<?xml version="1.0" standalone="no"?>""")
        .append("""<!DOCTYPE svg PUBLIC "-//W3C//DTD SVG 1.1//EN" 
                     |"http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd">""".trimMargin())
        .append("""<svg version="1.1" xmlns="http://www.w3.org/2000/svg">""")
        .append("""<text x="20" y="30" font-size="18" font-family="Inconsolata, monospace">
                |${constructionString}</text>""".trimMargin())

    private var leftOffset = 0
    private val typeInfo = mutableListOf<TypeInfo>()

    private fun traverseValue(value: Value) {
        typeInfo.add(
            TypeInfo(
                typename = value.typename,
                level = value.depth,
                leftOffset = leftOffset
            )
        )
    }

    private fun traverseComposite(composite: Composite) {
        val oldOffset = leftOffset

        typeInfo.add(
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

        typeInfo.add(
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

    private fun add(ti: TypeInfo) {
        val y = 30 + ti.level * levelSize
        val x = 20 + ti.leftOffset * charWidth
        builder.append(
            """<text x="$x" y="$y" font-size="18" font-family="Inconsolata, monospace">${ti.typename}</text>"""
        )
    }

    private fun build() = typeInfo.forEach(::add)

    private fun formSvg(): String {

        traverse(tree)
        build()

        return builder.append("</svg>").toString()
    }

}
