package org.fpeterek.tilscript.interpreter.interpreter

import org.fpeterek.tilscript.interpreter.sentence.Construction
import org.fpeterek.tilscript.interpreter.sentence.Variable

class LambdaFunction(
    args: List<Variable>,
    body: Construction,
    val context: LambdaContext,
) : DefaultFunction("<Lambda>", body.constructedType, args, body)
