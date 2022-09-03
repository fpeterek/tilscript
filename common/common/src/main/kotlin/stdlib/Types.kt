package org.fpeterek.tilscript.common.stdlib

import org.fpeterek.tilscript.common.types.AtomicType

object Types {
    val Bool  = AtomicType("Bool")
    val Indiv = AtomicType("Indiv")
    val Time  = AtomicType("Time")
    val World = AtomicType("World")
    val Real  = AtomicType("Real")
    val Int   = AtomicType("Int")
    val Type  = AtomicType("Type")
    val Text  = AtomicType("Text")

    val all = listOf(
        Bool, Indiv, Time, World, Real, Int, Type
    )
}