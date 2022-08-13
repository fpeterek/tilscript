package org.fpeterek.til.interpreter.interpreter.builtins

import org.fpeterek.til.interpreter.interpreter.OperatorFunction
import org.fpeterek.til.interpreter.interpreter.interpreterinterface.InterpreterInterface
import org.fpeterek.til.interpreter.sentence.*
import org.fpeterek.til.interpreter.types.Type
import org.fpeterek.til.interpreter.util.SrcPosition

object NumericOperators {

    val realArgs get() = listOf(
        Variable("fst", SrcPosition(-1, -1), Types.Real),
        Variable("snd", SrcPosition(-1, -1), Types.Real),
    )

    abstract class NumericOperatorBase(op: String, returnType: Type) : OperatorFunction(
        op,
        returnType,
        realArgs
    ) {

        protected val noPos get() = SrcPosition(-1, -1)

        protected abstract fun calcIntegral(fst: Integral, snd: Integral, interpreter: InterpreterInterface): Construction
        protected abstract fun calcReal(fst: Real, snd: Real, interpreter: InterpreterInterface): Construction

        override fun apply(interpreter: InterpreterInterface, args: List<Construction>): Construction {

            val int = args.map(interpreter::interpret)

            val t1 = int[0].constructionType
            val t2 = int[1].constructionType

            if (int[0] is Nil) {
                return int[0]
            }

            if (int[1] is Nil) {
                return int[1]
            }

            if (!interpreter.typesMatch(t1, t2)) {
                throw RuntimeException("Argument type mismatch in operator $name (first: ${t1}, second: ${t2}")
            }

            if (!interpreter.typesMatch(t1, Types.Int) && !interpreter.typesMatch(t1, Types.Real)) {
                throw RuntimeException("Invalid argument type for operator $name, (received: $t1, expected Real or Int)")
            }

            if (int.any { it !is Integral && it !is Real }) {
                return interpreter.nil
            }

            return when (int[0]) {
                is Integral -> calcIntegral(int[0] as Integral, int[1] as Integral, interpreter)
                else        -> calcReal(int[0] as Real, int[1] as Real, interpreter)
            }
        }

    }

    object Plus : NumericOperatorBase("+", Types.Real) {
        override fun calcIntegral(fst: Integral, snd: Integral, interpreter: InterpreterInterface) =
            Integral(fst.value + snd.value, noPos)

        override fun calcReal(fst: Real, snd: Real, interpreter: InterpreterInterface) =
            Real(fst.value + snd.value, noPos)
    }

    object Minus : NumericOperatorBase("-", Types.Real) {
        override fun calcIntegral(fst: Integral, snd: Integral, interpreter: InterpreterInterface) =
            Integral(fst.value - snd.value, noPos)

        override fun calcReal(fst: Real, snd: Real, interpreter: InterpreterInterface) =
            Real(fst.value - snd.value, noPos)
    }

    object Multiply : NumericOperatorBase("*", Types.Real) {
        override fun calcIntegral(fst: Integral, snd: Integral, interpreter: InterpreterInterface) =
            Integral(fst.value * snd.value, noPos)

        override fun calcReal(fst: Real, snd: Real, interpreter: InterpreterInterface) =
            Real(fst.value * snd.value, noPos)
    }

    object Divide : NumericOperatorBase("/", Types.Real) {
        override fun calcIntegral(fst: Integral, snd: Integral, interpreter: InterpreterInterface) = when (snd.value) {
            0L   -> interpreter.nil
            else -> Integral(fst.value / snd.value, noPos)
        }

        override fun calcReal(fst: Real, snd: Real, interpreter: InterpreterInterface) = when (snd.value) {
            0.0  -> interpreter.nil
            else -> Real(fst.value / snd.value, noPos)
        }
    }

    object Less : NumericOperatorBase("<", Types.Bool) {
        override fun calcIntegral(fst: Integral, snd: Integral, interpreter: InterpreterInterface) = when {
            fst.value < snd.value -> Values.True
            else -> Values.False
        }

        override fun calcReal(fst: Real, snd: Real, interpreter: InterpreterInterface) = when {
            fst.value < snd.value -> Values.True
            else -> Values.False
        }
    }

    object Greater : NumericOperatorBase(">", Types.Bool) {
        override fun calcIntegral(fst: Integral, snd: Integral, interpreter: InterpreterInterface) = when {
            fst.value > snd.value -> Values.True
            else -> Values.False
        }

        override fun calcReal(fst: Real, snd: Real, interpreter: InterpreterInterface) = when {
            fst.value > snd.value -> Values.True
            else -> Values.False
        }
    }

}

















