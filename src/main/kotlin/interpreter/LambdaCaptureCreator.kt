package org.fpeterek.tilscript.interpreter.interpreter

import org.fpeterek.tilscript.interpreter.sentence.*

class LambdaCaptureCreator(private val lambdaFrame: StackFrame) {

    private fun getVariable(variable: Variable, frame: StackFrame? = lambdaFrame, isLocal: Boolean = true): Pair<Variable, Boolean> {

        if (frame == null) {
            throw RuntimeException("Variable '${variable.name}' not found when creating lambda capture")
        }

        if (variable.name in frame) {
            return frame[variable.name]!! to isLocal
        }

        val local = if (lambdaFrame === frame) {
            false
        } else {
            isLocal
        }

        return getVariable(variable, frame.parent, local)
    }

    private fun captureVars(composition: Composition): List<Variable> =
        captureVars(composition.function) + composition.args.flatMap(::captureVars)

    private fun captureVars(execution: Execution): List<Variable> = captureVars(execution.construction)

    private fun captureVars(variable: Variable): List<Variable> {
        val (varFromStack, isLocal) = getVariable(variable)

        return when (isLocal) {
            true -> listOf()
            else -> listOf(varFromStack)
        }
    }

    fun captureVars(construction: Construction): List<Variable> = when (construction) {
        // An inner closure will get its own capture
        is Closure        -> listOf()
        // As of now, trivialization bound variables are assumed to be defined at top-level
        is Trivialization -> listOf()
        // Values do not need to be captured
        is Value          -> listOf()
        // Functions only refer to a function and thus there is nothing to be captured
        is TilFunction    -> listOf()

        is Composition    -> captureVars(construction)
        is Execution      -> captureVars(construction)
        is Variable       -> captureVars(construction)
    }

}
