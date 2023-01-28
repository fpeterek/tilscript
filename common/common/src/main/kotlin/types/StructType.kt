package org.fpeterek.tilscript.common.types

import org.fpeterek.tilscript.common.die
import org.fpeterek.tilscript.common.sentence.Variable
import org.fpeterek.tilscript.common.types.Util.isGeneric

class StructType(
    override val name: String,
) : Type() {

    var frozen = false
        private set

    private val attrNames = mutableSetOf<String>()
    private val attrs = mutableListOf<Variable>()

    val attributes: List<Variable> get() = attrs

    fun addAttribute(attr: Variable) {
        if (frozen) {
            throw RuntimeException("Cannot mutate a frozen struct definition")
        }
        if (attr.constructedType.isGeneric) {
            die("Struct attributes cannot be generic", attr.position)
        }

        attrs.add(attr)
        attrNames.add(attr.name)
    }

    fun has(attr: String) = attr in attrNames

    fun freeze() {
        frozen = true
    }
}