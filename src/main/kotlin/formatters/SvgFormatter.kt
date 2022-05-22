package org.fpeterek.til.typechecking.formatters

import org.fpeterek.til.typechecking.formatters.svginternals.*
import org.fpeterek.til.typechecking.sentence.*
import java.util.ListResourceBundle
import kotlin.math.round


class SvgFormatter private constructor(private val construction: Construction) {

    companion object {
        fun format(constructions: List<Construction>): String = SvgFormatter(constructions.first()).formSvg()
    }

    private val tree get() = SvgTreeCreator.createTree(construction)
    private val blobs = SvgTreeProcessor.process(construction, tree)
    private val alignments = TreeAlignment.getAlignments(blobs)

    private val levelSize = 80
    private val charWidth = 450/50.0

    private object Begin {
        const val x = 20
        const val y = 30
    }

    private val builder = StringBuilder()
        .append("""<?xml version="1.0" standalone="no"?>""")
        .append("""<!DOCTYPE svg PUBLIC "-//W3C//DTD SVG 1.1//EN" 
                     |"http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd">""".trimMargin())
        .append("""<svg version="1.1" xmlns="http://www.w3.org/2000/svg">""")

    private fun calcPosition(leftOffset: Int, level: Int): Point = Point(
        x = Begin.x + leftOffset*charWidth + charWidth/2,
        y = Begin.y + level*levelSize.toDouble()
    )

    private fun cutLine(line: Line): Line = Line(
        Point(
            line.begin.x,
            line.begin.y + 3,
        ),
        Point(
            line.end.x,
            line.end.y - 15
        )
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

    private fun drawSingleLine(line: Line) {
        val (begin, end) = line

        val (x1, y1) = begin
        val (x2, y2) = end

        builder.append("""<line x1="$x1" y1="$y1" x2="$x2" y2="$y2" style="stroke:rgb(255,0,0);stroke-width=2" />""")
    }

    private fun round(point: Point) = Point(
        round(point.x * 1) / 1.0,
        round(point.y * 1) / 1.0,
    )

    private fun prettify(line: Line): List<Line> {

        val (begin, end) = line

        val p1 = round(begin)
        val p4 = round(end)
        val p2 = Point(p1.x, p4.y - 15)
        val p3 = Point(p4.x, p4.y - 15)

        return listOf(
            Line(p1, p2),
            Line(p2, p3),
            Line(p3, p4)
        )
    }

    private fun drawLine(line: Line) = prettify(line).forEach(::drawSingleLine)

    private fun buildLines(blob: TextBlob) {

        val end = calcPosition(alignments[blob.offset], blob.level)
        val beginnings = blob.children.asSequence().map { calcPosition(alignments[it.offset], it.level) }

        beginnings
            .zip(generateSequence { end })
            .map { (begin, end) -> Line(begin, end) }
            .map(::cutLine)
            .forEach(::drawLine)
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
