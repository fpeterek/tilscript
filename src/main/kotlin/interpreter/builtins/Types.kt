package org.fpeterek.til.interpreter.interpreter.builtins

import org.fpeterek.til.interpreter.types.AtomicType

object Types {
    val Bool  = AtomicType("Bool", "Truth values")
    val Indiv = AtomicType("Indiv", "Individuals")
    val Time  = AtomicType("Time", "Timestamps")
    val World = AtomicType("World", "Worlds")
    val Real  = AtomicType("Real", "Real numbers")
    val Int   = AtomicType("Int", "Whole numbers")
    val Type  = AtomicType("Type", "Type reference")
    val Text  = AtomicType("Text", "Strings")

    val all = listOf(
        Bool, Indiv, Time, World, Real, Int, Type
    )
}