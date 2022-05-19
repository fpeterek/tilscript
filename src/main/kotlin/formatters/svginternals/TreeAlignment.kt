package org.fpeterek.til.typechecking.formatters.svginternals

class TreeAlignment private constructor(private val textBlobs: List<List<TextBlob>>) {

    companion object {
        fun getAlignments(textBlobs: List<List<TextBlob>>) = TreeAlignment(textBlobs).align()
    }

    private fun maxOffset(blobs: List<TextBlob>) = blobs.maxOf { it.offset }

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

    private fun align(row: List<TextBlob>) {
        var lengthOfPrevious = row.first().offset

        row.forEach {
            storeAlignment(it.offset, lengthOfPrevious)
            lengthOfPrevious += it.length
        }
    }

    private fun align(): List<Int> {
        textBlobs.forEach(::align)

        return alignments
    }

}