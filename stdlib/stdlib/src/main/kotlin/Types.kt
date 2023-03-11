package org.fpeterek.tilscript.stdlib

import org.fpeterek.tilscript.common.types.Primitives

object Types {
    val Bool        = Primitives.Bool
    val Type        = Primitives.Type
    val Int         = Primitives.Int
    val Text        = Primitives.Text
    val Indiv       = Primitives.Indiv
    val Real        = Primitives.Real
    val Time        = Primitives.Time
    val World       = Primitives.World
    val DeviceState = Primitives.DeviceState

    val all = listOf(Bool, Type, Int, Text, Indiv, Real, Time, World, DeviceState)
}