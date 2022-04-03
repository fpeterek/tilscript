package org.fpeterek.til.typechecking.astprocessing.result

sealed class Entity : IntermediateResult() {

    companion object {
        fun from(ir: IntermediateResult) = when (ir) {
            is Numeric -> Number(ir.value)
            is Symbol -> FnOrEntity(ir.symbol)
            is EntityName -> FnOrEntity(ir.name)
            else -> throw RuntimeException("Parser error: unexpected type ${ir.javaClass}")
        }
    }

    class Number(val value: String) : Entity()
    class FnOrEntity(val value: String) : Entity()

}