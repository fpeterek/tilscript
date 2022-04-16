package org.fpeterek.til.typechecking.types

import org.fpeterek.til.typechecking.greek.GreekAlphabet

class GenericType(val argNumber: Int) : Type() {

    val name
        get() = "Any$argNumber"

    val shortName
        get() = "${GreekAlphabet.alpha}$argNumber"

    override fun toString() = shortName

}
