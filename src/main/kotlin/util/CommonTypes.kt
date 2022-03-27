package org.fpeterek.til.typechecking.util

import org.fpeterek.til.typechecking.types.AtomicType
import org.fpeterek.til.typechecking.types.FunctionType
import org.fpeterek.til.typechecking.types.Type
import org.fpeterek.til.typechecking.util.Util.intensionalize

object CommonTypes {
    val office = AtomicType.Iota.intensionalize()
    val proposition = AtomicType.Omicron.intensionalize()
    val extensionProperty = FunctionType(AtomicType.Omicron, AtomicType.Iota)
    val property = extensionProperty.intensionalize()
    val setOfSets = FunctionType(
        FunctionType(AtomicType.Omicron, extensionProperty),
        extensionProperty
    )
    val binaryOmicron = FunctionType(AtomicType.Omicron, AtomicType.Omicron, AtomicType.Omicron)

    fun setOfSets(alpha: Type) = FunctionType(
        FunctionType(AtomicType.Omicron, FunctionType(AtomicType.Omicron, alpha)),
        FunctionType(AtomicType.Omicron, alpha)
    )
}