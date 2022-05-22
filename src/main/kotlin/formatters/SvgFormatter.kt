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

    private val levelSize = 60
    private val charWidth = 449/50.0

    private object Begin {
        const val x = 20
        const val y = 30
    }

    private val builder = StringBuilder()
        .append("""<?xml version="1.0" standalone="no"?>""")
        .append("""<!DOCTYPE svg PUBLIC "-//W3C//DTD SVG 1.1//EN" 
                     |"http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd">""".trimMargin())
        .append("""<svg version="1.1" xmlns="http://www.w3.org/2000/svg">""")

    private fun calcPosition(leftOffset: Int, level: Int): Pair<Double, Double> = Pair(
        Begin.x + leftOffset*charWidth + charWidth/2,
        Begin.y + level*levelSize.toDouble()
    )

    private fun buildLevel(blobs: List<TextBlob>, level: Int) {

        val y = Begin.y + level * levelSize
        val x = Begin.x
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

    private fun buildText() = blobs.forEachIndexed { index, textBlobs -> buildLevel(textBlobs, index) }

    private fun buildLines(blob: TextBlob) {

        val (x2, y2) = calcPosition(alignments[blob.offset], blob.level)

        blob.children.forEach {
            val (x1, y1) = calcPosition(alignments[it.offset], it.level)
            builder.append("""<line x1="$x1" y1="$y1" x2="$x2" y2="$y2" style="stroke:rgb(255,0,0);stroke-width=2" />""")
        }
    }

    private fun buildLines(blobs: List<TextBlob>): Unit =
        blobs.forEach(::buildLines)

    private fun buildLines(): Unit = blobs.forEach(::buildLines)

    private fun formSvg(): String {

        buildLines()
        buildText()

        return builder.append("</svg>").toString()
    }

}
