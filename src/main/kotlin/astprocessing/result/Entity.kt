package org.fpeterek.til.interpreter.astprocessing.result

import org.fpeterek.til.interpreter.util.SrcPosition

sealed class Entity(srcPos: SrcPosition) : IntermediateResult(srcPos) {

    companion object {
        fun from(ir: IntermediateResult) = when (ir) {
            is Numeric -> Number(ir.value, ir.position)
            is Symbol -> FnOrEntity(ir.symbol, ir.position)
            is EntityName -> FnOrEntity(ir.name, ir.position)
            is StringLit -> ir
            else -> throw RuntimeException("Parser error: unexpected type ${ir.javaClass}")
        }
    }

    class Number(val value: String, srcPos: SrcPosition) : Entity(srcPos)
    class FnOrEntity(val value: String, srcPos: SrcPosition) : Entity(srcPos)
    class StringLit(val value: String, srcPos: SrcPosition) : Entity(srcPos)

}