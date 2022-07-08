package org.fpeterek.til.typechecking.interpreter.functioninterface

import org.fpeterek.til.typechecking.interpreter.InterpreterInterface
import org.fpeterek.til.typechecking.sentence.Construction
import org.fpeterek.til.typechecking.types.Type

abstract class EagerFunction(name: String, returns: Type, args: List<Type>): FunctionInterface(name, returns, args) {

    override fun invoke(interpreter: InterpreterInterface, args: List<Construction>) = apply(interpreter, args)

}
