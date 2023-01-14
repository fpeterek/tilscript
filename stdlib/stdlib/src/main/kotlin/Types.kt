package org.fpeterek.tilscript.stdlib

import org.fpeterek.tilscript.common.types.ConstructionType
import org.fpeterek.tilscript.common.types.EmptyListType
import org.fpeterek.tilscript.common.types.Primitives

object Types {
    val Bool  = Primitives.Bool
    val Type  = Primitives.Type
    val Int   = Primitives.Int
    val Text  = Primitives.Text
    val Indiv = Primitives.Indiv
    val Real  = Primitives.Real
    val Time  = Primitives.Time
    val World = Primitives.World

    val Construction = ConstructionType
    val EmptyList = EmptyListType

    val all = listOf(Bool, Type, Int, Text, Indiv, Real, Time, World, Construction, EmptyList)
}