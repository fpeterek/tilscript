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

            val intArgs = args.map(interpreter::interpret)

            if (!interpreter.typesMatch(intArgs[0].constructionType, Builtins.Real)) {
                return interpreter.nil
            }

            if (!interpreter.typesMatch(intArgs[1].constructionType, Builtins.Real)) {
                return interpreter.nil
            }

            // Return Nil for symbolic values, i.e. 'Pi, 'E, 'SqrtTwo
            if (intArgs.any { it !is Real }) {
                return interpreter.nil
            }

            return calcValue(intArgs[0] as Real, intArgs[1] as Real, interpreter)
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