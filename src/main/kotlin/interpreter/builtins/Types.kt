package org.fpeterek.tilscript.interpreter.interpreter.builtins

import org.fpeterek.tilscript.interpreter.types.AtomicType

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