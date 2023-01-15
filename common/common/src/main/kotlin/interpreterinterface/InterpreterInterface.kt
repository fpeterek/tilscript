package org.fpeterek.tilscript.common.interpreterinterface

import org.fpeterek.tilscript.common.sentence.Construction
import org.fpeterek.tilscript.common.sentence.Nil
import org.fpeterek.tilscript.common.sentence.TilFunction
import org.fpeterek.tilscript.common.sentence.Variable
import org.fpeterek.tilscript.common.types.FunctionType
import org.fpeterek.tilscript.common.SrcPosition
import org.fpeterek.tilscript.common.types.Type


interface InterpreterInterface {
    val nil: Nil get() = Nil(SrcPosition(-1, -1))

    fun interpret(construction: Construction): Construction

    fun typesMatch(t1: Type, t2: Type): Boolean

    fun fnArgsMatch(fn: FunctionType, types: List<Type>): List<Boolean>

    fun fnSignatureMatch(fn: FunctionType, returned: Type, args: List<Type>): Pair<Boolean, List<Boolean>>

    fun createLocal(variable: Variable, value: Construction)

    fun getVariable(name: String): Variable?

    fun getFunction(name: String): TilFunction?

    fun getType(name: String): Type?
}