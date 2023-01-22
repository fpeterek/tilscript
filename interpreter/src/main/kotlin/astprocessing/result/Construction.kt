package org.fpeterek.tilscript.interpreter.astprocessing.result

import org.fpeterek.tilscript.common.SrcPosition

sealed class Construction(srcPos: SrcPosition) : IntermediateResult(srcPos) {

    fun extensionalize(srcPos: SrcPosition) = Composition(
        Composition(
            this,
            listOf(VarRef("w", srcPos)),
            srcPos
        ),
        listOf(VarRef("t", srcPos)),
        srcPos
    )

    class VarRef(val name: String, srcPos: SrcPosition) : Construction(srcPos) {
        constructor(varName: VarName, srcPos: SrcPosition) : this(varName.name, srcPos)
    }

    class AttributeRef(val names: List<String>, srcPos: SrcPosition) : Construction(srcPos) {
        constructor(varNames: List<VarName>, srcPos: SrcPosition) : this(varNames.map { it.name }, srcPos)
    }

    class Closure(
        val vars: List<TypedVar>,
        val construction: Construction,
        val returnType: DataType?,
        srcPos: SrcPosition
    ) : Construction(srcPos) {
        constructor(vars: TypedVars, construction: Construction, returnType: DataType?, srcPos: SrcPosition) :
            this(vars.vars, construction, returnType, srcPos)
    }

    class Execution(val order: Int, val construction: IntermediateResult, srcPos: SrcPosition) :
        Construction(srcPos)

    class Composition(val fn: Construction, val args: List<Construction>, srcPos: SrcPosition) :
        Construction(srcPos)
}