package org.fpeterek.til.typechecking.formatters.svginternals

import kotlin.math.max

class TreeAlignment private constructor(private val textBlobs: List<List<TextBlob>>) {

    companion object {
        fun getAlignments(textBlobs: List<List<TextBlob>>) = TreeAlignment(textBlobs).align()
    }

    private fun maxOffset() = textBlobs.first().last().offset

    // Offset level -> Actual position
    private val alignments = (0 .. maxOffset()).toMutableList()

    private fun storeAlignment(offset: Int, real: Int) {
        val delta = real - alignments[offset]

        if (delta <= 0) {
            return
        }

        (offset .. alignments.lastIndex).forEach {
            alignments[it] += delta
        }
    }

    private fun align(row: List<TextBlob>, index: Int) {
        var endOfPrevious = row.first().offset

        val padding = when (index) {
            0 -> 0
            else -> 3
        }

        row.forEach {
            endOfPrevious = max(endOfPrevious, alignments[it.offset])
            storeAlignment(it.offset, endOfPrevious)
            endOfPrevious += it.length + padding
        }
    }

    private fun align(): List<Int> {
        textBlobs.forEachIndexed { idx, line -> align(line, idx) }

        return alignments
    }

}