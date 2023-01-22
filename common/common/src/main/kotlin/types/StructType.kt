package org.fpeterek.tilscript.common.types

import org.fpeterek.tilscript.common.die
import org.fpeterek.tilscript.common.sentence.Variable
import org.fpeterek.tilscript.common.types.Util.isGeneric

class StructType(
    override val name: String,
    val attributes: List<Variable>
) : Type() {

    init {
        attributes.forEach {
            if (it.constructedType.isGeneric) {
                die("Struct attributes cannot be generic", it.position)
            }
        }
    }

    private val attrNames = attributes.asSequence().map { it.name }.toSet()

    fun has(attr: String) = attr in attrNames
}