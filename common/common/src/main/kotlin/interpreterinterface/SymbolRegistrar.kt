package org.fpeterek.tilscript.common.interpreterinterface

import org.fpeterek.tilscript.common.sentence.Symbol
import org.fpeterek.tilscript.common.sentence.TilFunction
import org.fpeterek.tilscript.common.sentence.Value
import org.fpeterek.tilscript.common.sentence.Variable
import org.fpeterek.tilscript.common.types.StructType
import org.fpeterek.tilscript.common.types.Type
import org.fpeterek.tilscript.common.types.TypeAlias

interface SymbolRegistrar {
    val functions: List<FunctionInterface>
    val functionDeclarations: List<TilFunction>
    val aliases: List<TypeAlias>
    val symbols: List<Symbol>
    val structs: List<StructType>
    val variables: List<Variable>
}
