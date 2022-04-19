package org.fpeterek.til.typechecking.types

import org.fpeterek.til.typechecking.greek.GreekAlphabet

class GenericType(val argNumber: Int) : Type() {

    override val name
        get() = "Any$argNumber"

    override val shortName
        get() = "${GreekAlphabet.alpha}$argNumber"

    override fun toString() = shortName

}
