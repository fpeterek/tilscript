package org.fpeterek.til.typechecking.interpreter.functioninterface

import org.fpeterek.til.typechecking.interpreter.InterpreterInterface
import org.fpeterek.til.typechecking.sentence.Construction
import org.fpeterek.til.typechecking.sentence.TilFunction
import org.fpeterek.til.typechecking.types.FunctionType
import org.fpeterek.til.typechecking.types.Type
import org.fpeterek.til.typechecking.util.SrcPosition

sealed class FunctionInterface constructor(
    val name: String,
    val returns: Type,
    val args: List<Type>,
) {

    val signature = FunctionType(returns, args)
    val tilFunction = TilFunction(name, SrcPosition(-1, -1), signature)

    abstract fun apply(interpreter: InterpreterInterface, args: List<Construction>): Construction

    abstract operator fun invoke(interpreter: InterpreterInterface, args: List<Construction>): Construction
}