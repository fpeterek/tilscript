package org.fpeterek.til.typechecking.astprocessing.result

class Closure(val vars: List<TypedVar>, val construction: IntermediateResult) :
        IntermediateResult() {

    constructor(vars: TypedVars, construction: IntermediateResult) :
        this(vars.vars, construction)

}
