package org.fpeterek.til.typechecking.interpreter.builtins

import org.fpeterek.til.typechecking.interpreter.OperatorFunction
import org.fpeterek.til.typechecking.interpreter.interpreterinterface.InterpreterInterface
import org.fpeterek.til.typechecking.sentence.Construction
import org.fpeterek.til.typechecking.sentence.Real
import org.fpeterek.til.typechecking.sentence.Variable
import org.fpeterek.til.typechecking.tilscript.Builtins
import org.fpeterek.til.typechecking.util.SrcPosition

object RealOperators {

    val realArgs = listOf(
        Variable("fst", SrcPosition(-1, -1), Builtins.Real),
        Variable("snd", SrcPosition(-1, -1), Builtins.Real),
    )

    abstract class RealOperatorBase(op: String) : OperatorFunction(
        op,
        Builtins.Real,
        realArgs
    ) {

        protected val noPos get() = SrcPosition(-1, -1)

        protected abstract fun calcValue(fst: Real, snd: Real, interpreter: InterpreterInterface): Construction

        override fun apply(interpreter: InterpreterInterface, args: List<Construction>): Construction {
            if (!interpreter.typesMatch(args[0].constructionType, Builtins.Real)) {
                return interpreter.nil
            }

            if (!interpreter.typesMatch(args[1].constructionType, Builtins.Real)) {
                return interpreter.nil
            }

            return calcValue(args[0] as Real, args[1] as Real, interpreter)
        }

    }

    object Plus : RealOperatorBase("+") {
        override fun calcValue(fst: Real, snd: Real, interpreter: InterpreterInterface) =
            Real(fst.value + snd.value, noPos)
    }

    object Minus : RealOperatorBase("-") {
        override fun calcValue(fst: Real, snd: Real, interpreter: InterpreterInterface) =
            Real(fst.value - snd.value, noPos)
    }

    object Multiply : RealOperatorBase("*") {
        override fun calcValue(fst: Real, snd: Real, interpreter: InterpreterInterface) =
            Real(fst.value * snd.value, noPos)
    }

    object Divide : RealOperatorBase("/") {
        override fun calcValue(fst: Real, snd: Real, interpreter: InterpreterInterface) = when (snd.value) {
            0.0  -> interpreter.nil
            else -> Real(fst.value / snd.value, noPos)
        }
    }

}