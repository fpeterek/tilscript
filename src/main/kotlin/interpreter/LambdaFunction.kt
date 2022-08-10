package org.fpeterek.til.interpreter.interpreter

import org.fpeterek.til.interpreter.sentence.Construction
import org.fpeterek.til.interpreter.sentence.Variable

class LambdaFunction(
    args: List<Variable>,
    body: Construction,
    val context: LambdaContext,
) : DefaultFunction("<Lambda>", body.constructedType, args, body)
