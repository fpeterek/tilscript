package org.fpeterek.tilscript.common.types


class AtomicType private constructor(
    override val name: String,
) : Type() {

    companion object {
        val Bool        = AtomicType("Bool")
        val Indiv       = AtomicType("Indiv")
        val Time        = AtomicType("Time")
        val World       = AtomicType("World")
        val DeviceState = AtomicType("DeviceState")
        val Real        = AtomicType("Real")
        val Int         = AtomicType("Int")
        val Type        = AtomicType("Type")
        val Text        = AtomicType("Text")
    }

}
