package org.fpeterek.tilscript.common.interpreterinterface

import org.fpeterek.tilscript.common.sentence.Construction
import org.fpeterek.tilscript.common.sentence.TilFunction
import org.fpeterek.tilscript.common.sentence.Variable
import org.fpeterek.tilscript.common.types.FunctionType
import org.fpeterek.tilscript.common.types.Type
import org.fpeterek.tilscript.common.SrcPosition
import org.fpeterek.tilscript.common.die

sealed class FunctionInterface constructor(
    val name: String,
    val returns: Type,
    val args: List<Variable>,
) {

    abstract val acceptsNil: Boolean

    val argTypes = args.map { it.constructedType }

    val signature = FunctionType(returns, argTypes)
    val tilFunction = TilFunction(name, SrcPosition(-1, -1), signature, implementation = this)

    protected fun checkArgCount(fnArgs: List<Construction>) {
        if (fnArgs.size != args.size) {
            die("Incorrect number of arguments in application of function '$name' (expected: ${args.size}, received: ${fnArgs.size})")
        }
    }

    private fun handleArgMatches(matches: List<Boolean>, fnArgs: List<Type>) {
        matches.forEachIndexed { idx, match ->
            if (!match) {
                val exp = argTypes[idx]
                val rec = fnArgs[idx]
                die("Invalid argument type in application of function '$name' (expected: $exp, received: $rec)")
            }
        }
    }

    protected fun checkArgTypes(interpreter: InterpreterInterface, fnArgs: List<Type>) = handleArgMatches(
        interpreter.fnArgsMatch(signature, fnArgs),
        fnArgs
    )

    protected fun checkSignature(interpreter: InterpreterInterface, returned: Type, fnArgs: List<Type>) {
        val (retvalMatch, argsMatches) = interpreter.fnSignatureMatch(signature, returned, fnArgs)

        if (!retvalMatch) {
            die("Type returned by function '$name' does not match expected return type (expected: $returns, received: $returned)")
        }

        handleArgMatches(argsMatches, fnArgs)
    }

    protected fun runWithTypechecks(interpreter: InterpreterInterface, args: List<Construction>, ctx: FnCallContext): Construction {
        checkArgCount(args)
        checkArgTypes(interpreter, args.map { it.constructionType })

        val retval = apply(interpreter, args, ctx)

        checkSignature(interpreter, retval.constructionType, args.map { it.constructionType })

        return retval
    }

    abstract fun apply(interpreter: InterpreterInterface, args: List<Construction>, ctx: FnCallContext): Construction

    abstract operator fun invoke(interpreter: InterpreterInterface, args: List<Construction>, ctx: FnCallContext): Construction
}