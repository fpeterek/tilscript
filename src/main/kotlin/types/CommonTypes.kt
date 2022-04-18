package org.fpeterek.til.typechecking.types

import org.fpeterek.til.typechecking.types.Util.intensionalize

object CommonTypes {
    val office get() = AtomicType.Iota.intensionalize()
    val proposition get() = AtomicType.Omicron.intensionalize()
    val extensionProperty get() = FunctionType(AtomicType.Omicron, AtomicType.Iota)
    val property get() = extensionProperty.intensionalize()
    val setOfSets get() = FunctionType(
        FunctionType(AtomicType.Omicron, extensionProperty),
        extensionProperty
    )
    val binaryOmicron get() = FunctionType(AtomicType.Omicron, AtomicType.Omicron, AtomicType.Omicron)

    fun setOfSets(alpha: Type) = FunctionType(
        FunctionType(AtomicType.Omicron, FunctionType(AtomicType.Omicron, alpha)),
        FunctionType(AtomicType.Omicron, alpha)
    )
}