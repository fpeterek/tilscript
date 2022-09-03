package org.fpeterek.tilscript.common.interpreterinterface

import org.fpeterek.tilscript.common.sentence.Construction
import org.fpeterek.tilscript.common.sentence.Variable
import org.fpeterek.tilscript.common.types.Type

class LambdaFunction(
    args: List<Variable>,
    body: Construction,
    val context: LambdaContext,
    returnType: Type
) : TilConstructionFunction("<Lambda>", returnType, args, body)
