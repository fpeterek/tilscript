package org.fpeterek.tilscript.interpreter.interpreter

import org.fpeterek.tilscript.interpreter.sentence.Construction
import org.fpeterek.tilscript.interpreter.sentence.Variable
import org.fpeterek.tilscript.interpreter.types.Type

class LambdaFunction(
    args: List<Variable>,
    body: Construction,
    val context: LambdaContext,
    returnType: Type
) : DefaultFunction("<Lambda>", returnType, args, body)
