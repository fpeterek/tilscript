package org.fpeterek.til.typechecking.interpreter.builtins

import org.fpeterek.til.typechecking.interpreter.OperatorFunction
import org.fpeterek.til.typechecking.interpreter.interpreterinterface.InterpreterInterface
import org.fpeterek.til.typechecking.sentence.Construction
import org.fpeterek.til.typechecking.sentence.Integral
import org.fpeterek.til.typechecking.sentence.Variable
import org.fpeterek.til.typechecking.tilscript.Builtins
import org.fpeterek.til.typechecking.util.SrcPosition

object IntOperators {

    val intArgs = listOf(
        Variable("fst", SrcPosition(-1, -1), Builtins.Int),
        Variable("snd", SrcPosition(-1, -1), Builtins.Int),
    )

    abstract class IntOperatorBase(op: String) : OperatorFunction(
        op,
        Builtins.Int,
        intArgs
    ) {

        protected val noPos get() = SrcPosition(-1, -1)

        protected abstract fun calcValue(fst: Integral, snd: Integral, interpreter: InterpreterInterface): Construction

        override fun apply(interpreter: InterpreterInterface, args: List<Construction>): Construction {

            val intArgs = args.map { interpreter.interpret(it) }

            if (!interpreter.typesMatch(intArgs[0].constructionType, Builtins.Int)) {
                return interpreter.nil
            }

            if (!interpreter.typesMatch(intArgs[1].constructionType, Builtins.Int)) {
                return interpreter.nil
            }

            if (intArgs.any { it !is Integral }) {
                return interpreter.nil
            }

            return calcValue(intArgs[0] as Integral, intArgs[1] as Integral, interpreter)
        }

    }

    object Plus : IntOperatorBase("+") {
        override fun calcValue(fst: Integral, snd: Integral, interpreter: InterpreterInterface) =
            Integral(fst.value + snd.value, noPos)
    }

    object Minus : IntOperatorBase("-") {
        override fun calcValue(fst: Integral, snd: Integral, interpreter: InterpreterInterface) =
            Integral(fst.value - snd.value, noPos)
    }

    object Multiply : IntOperatorBase("*") {
        override fun calcValue(fst: Integral, snd: Integral, interpreter: InterpreterInterface) =
            Integral(fst.value * snd.value, noPos)
    }

    object Divide : IntOperatorBase("/") {
        override fun calcValue(fst: Integral, snd: Integral, interpreter: InterpreterInterface) = when (snd.value) {
            0L   -> interpreter.nil
            else -> Integral(fst.value / snd.value, noPos)
        }
    }

}

















