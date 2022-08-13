package org.fpeterek.tilscript.interpreter.astprocessing.result

import org.fpeterek.tilscript.interpreter.util.SrcPosition

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

    class Closure(val vars: List<TypedVar>, val construction: Construction, srcPos: SrcPosition) :
        Construction(srcPos) {
        constructor(vars: TypedVars, construction: Construction, srcPos: SrcPosition) :
                this(vars.vars, construction, srcPos)
    }

    class Execution(val order: Int, val construction: IntermediateResult, srcPos: SrcPosition) :
        Construction(srcPos)

    class Composition(val fn: Construction, val args: List<Construction>, srcPos: SrcPosition) :
        Construction(srcPos)
}