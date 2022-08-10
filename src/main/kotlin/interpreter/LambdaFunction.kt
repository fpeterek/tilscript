package org.fpeterek.til.typechecking.interpreter

import org.fpeterek.til.typechecking.sentence.Construction
import org.fpeterek.til.typechecking.sentence.Variable

class LambdaFunction(
    args: List<Variable>,
    body: Construction,
    val context: LambdaContext,
) : DefaultFunction("<Lambda>", body.constructedType, args, body)
