package org.fpeterek.til.typechecking.formatters.svginternals

class TreeAlignment private constructor(private val textBlobs: List<List<TextBlob>>) {

    companion object {
        fun getAlignments(textBlobs: List<List<TextBlob>>) = TreeAlignment(textBlobs).align()
    }

    private fun maxOffset(blobs: List<TextBlob>): Int {

        blobs.forEach { print("${it.offset}: $it, ") }
        println()

        return blobs.last().offset
    }

    private fun maxOffset() = textBlobs.maxOf { maxOffset(it) }

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

    // TODO: Fix padding
    private fun align(row: List<TextBlob>, index: Int) {
        var endOfPrevious = row.first().offset

        val padding = when (index) {
            0 -> 0
            else -> 3
        }

        row.forEach {
            storeAlignment(it.offset, endOfPrevious)
            endOfPrevious += it.length + padding
        }
    }

    private fun align(): List<Int> {
        textBlobs.forEachIndexed { idx, line -> align(line, idx) }

        return alignments
    }

}