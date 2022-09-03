package org.fpeterek.tilscript.common.interpreterinterface

import org.fpeterek.tilscript.common.sentence.TilFunction
import org.fpeterek.tilscript.common.sentence.Value
import org.fpeterek.tilscript.common.types.Type

interface SymbolRegistrar {
    val functions: List<FunctionInterface>
    val functionDeclarations: List<TilFunction>
    val types: List<Type>
    val values: List<Value>
}