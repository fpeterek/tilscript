package org.fpeterek.til.typechecking.formatters

import org.fpeterek.til.typechecking.formatters.svginternals.*
import org.fpeterek.til.typechecking.sentence.*


class SvgFormatter private constructor(private val construction: Construction) {

    companion object {
        fun format(constructions: List<Construction>): String = SvgFormatter(constructions.first()).formSvg()
    }

    private val tree get() = SvgTreeCreator.createTree(construction)
    private val blobs = SvgTreeProcessor.process(construction, tree)
    private val alignments = TreeAlignment.getAlignments(blobs)

    private val levelSize = 20

    private val builder = StringBuilder()
        .append("""<?xml version="1.0" standalone="no"?>""")
        .append("""<!DOCTYPE svg PUBLIC "-//W3C//DTD SVG 1.1//EN" 
                     |"http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd">""".trimMargin())
        .append("""<svg version="1.1" xmlns="http://www.w3.org/2000/svg">""")

    private fun buildLevel(blobs: List<TextBlob>, level: Int) {

        val y = 30 + level * levelSize
        val x = 20
        var prevLen = 0

        builder.append("""<text xml:space="preserve" x="$x" y="$y" font-size="18px" font-family="Inconsolata, monospace">""")

        blobs.forEach {
            val paddingLen = alignments[it.offset] - prevLen
            val padding = when {
                paddingLen > 0 -> " ".repeat(paddingLen)
                else -> ""
            }
            prevLen += padding.length + it.length

            builder.append(padding)
            builder.append(it.text)
        }

        builder.append("</text>")
    }

    private fun build() = blobs.forEachIndexed { index, textBlobs -> buildLevel(textBlobs, index) }

    private fun formSvg(): String {

        build()

        return builder.append("</svg>").toString()
    }

}
