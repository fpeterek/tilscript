package org.fpeterek.til.typechecking.astprocessing.result

import org.fpeterek.til.typechecking.util.SrcPosition

class FunDefinition(
    val name: EntityName,
    val args: List<TypedVar>,
    val consType: DataType,
    val cons: Construction,
    srcPos: SrcPosition,
) : IntermediateResult(srcPos) {

    constructor(name: EntityName, args: TypedVars, consType: DataType, cons: Construction, srcPos: SrcPosition) :
            this(name, args.vars, consType, cons, srcPos)

}