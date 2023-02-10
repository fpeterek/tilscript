package org.fpeterek.tilscript.interpreter.astprocessing.result

import org.fpeterek.tilscript.common.SrcPosition

sealed class Entity(srcPos: SrcPosition) : IntermediateResult(srcPos) {

    companion object {
        fun from(ir: IntermediateResult) = when (ir) {
            is Numeric          -> Number(ir.value, ir.position)
            is Symbol           -> FnOrEntity(ir.symbol, ir.position)
            is EntityName       -> FnOrEntity(ir.name, ir.position)
            is StringLit        -> ir
            is Construction.Nil -> FnOrEntity("Nil", ir.position)

            else -> throw RuntimeException("Parser error: unexpected type ${ir.javaClass}")
        }
    }

    class Number(val value: String, srcPos: SrcPosition) : Entity(srcPos)
    class FnOrEntity(val value: String, srcPos: SrcPosition) : Entity(srcPos)
    class StringLit(val value: String, srcPos: SrcPosition) : Entity(srcPos)

}