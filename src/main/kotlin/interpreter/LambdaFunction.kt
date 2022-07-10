package org.fpeterek.til.typechecking.interpreter

import org.fpeterek.til.typechecking.interpreter.interpreterinterface.InterpreterInterface
import org.fpeterek.til.typechecking.sentence.Construction
import org.fpeterek.til.typechecking.sentence.Variable

class LambdaFunction(
    args: List<Variable>,
    body: Construction,
    val context: LambdaContext,
) : DefaultFunction("<Lambda>", body.constructedType, args, body) {

    override fun apply(interpreter: InterpreterInterface, args: List<Construction>): Construction {
        context.captureList
            .filter { it.value != null }
            .forEach { interpreter.createLocal(it, it.value!!) }

        return super.apply(interpreter, args)
    }

}