package org.fpeterek.tilscript.common.interpreterinterface

import org.fpeterek.tilscript.common.sentence.Value
import org.fpeterek.tilscript.common.types.Type

interface SymbolRegistrar {
    val functions: List<FunctionInterface>
    val types: List<Type>
    val values: List<Value>
}