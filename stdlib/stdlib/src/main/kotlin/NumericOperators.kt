package org.fpeterek.tilscript.stdlib

import org.fpeterek.tilscript.common.interpreterinterface.OperatorFunction
import org.fpeterek.tilscript.common.interpreterinterface.FnCallContext
import org.fpeterek.tilscript.common.interpreterinterface.InterpreterInterface
import org.fpeterek.tilscript.common.sentence.*
import org.fpeterek.tilscript.common.types.Type
import org.fpeterek.tilscript.common.SrcPosition
import org.fpeterek.tilscript.common.die

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

        protected abstract fun calcIntegral(fst: Integral, snd: Integral, interpreter: InterpreterInterface, ctx: FnCallContext): Construction
        protected abstract fun calcReal(fst: Real, snd: Real, interpreter: InterpreterInterface, ctx: FnCallContext): Construction

        override fun apply(interpreter: InterpreterInterface, args: List<Construction>, ctx: FnCallContext): Construction {

            val t1 = args[0].constructionType
            val t2 = args[1].constructionType

            if (!interpreter.typesMatch(t1, t2)) {
                die("Argument type mismatch in operator $name (first: ${t1}, second: ${t2}")
            }

            if (!interpreter.typesMatch(t1, Types.Int) && !interpreter.typesMatch(t1, Types.Real)) {
                die("Invalid argument type for operator $name, (received: $t1, expected Real or Int)")
            }

            if (args.any { it !is Integral && it !is Real }) {
                return Nil(ctx.position, reason="Cannot perform arithmetic operations on symbolic values")
            }

            return when (args[0]) {
                is Integral -> calcIntegral(args[0] as Integral, args[1] as Integral, interpreter, ctx)
                else        -> calcReal(args[0] as Real, args[1] as Real, interpreter, ctx)
            }
        }

    }

    object Plus : NumericOperatorBase("+", Types.Real) {
        override fun calcIntegral(fst: Integral, snd: Integral, interpreter: InterpreterInterface, ctx: FnCallContext) =
            Integral(fst.value + snd.value, noPos)

        override fun calcReal(fst: Real, snd: Real, interpreter: InterpreterInterface, ctx: FnCallContext) =
            Real(fst.value + snd.value, noPos)
    }

    object Minus : NumericOperatorBase("-", Types.Real) {
        override fun calcIntegral(fst: Integral, snd: Integral, interpreter: InterpreterInterface, ctx: FnCallContext) =
            Integral(fst.value - snd.value, noPos)

        override fun calcReal(fst: Real, snd: Real, interpreter: InterpreterInterface, ctx: FnCallContext) =
            Real(fst.value - snd.value, noPos)
    }

    object Multiply : NumericOperatorBase("*", Types.Real) {
        override fun calcIntegral(fst: Integral, snd: Integral, interpreter: InterpreterInterface, ctx: FnCallContext) =
            Integral(fst.value * snd.value, noPos)

        override fun calcReal(fst: Real, snd: Real, interpreter: InterpreterInterface, ctx: FnCallContext) =
            Real(fst.value * snd.value, noPos)
    }

    object Divide : NumericOperatorBase("/", Types.Real) {
        override fun calcIntegral(fst: Integral, snd: Integral, interpreter: InterpreterInterface, ctx: FnCallContext) = when (snd.value) {
            0L   -> Nil(ctx.position, reason="Division by zero")
            else -> Integral(fst.value / snd.value, noPos)
        }

        override fun calcReal(fst: Real, snd: Real, interpreter: InterpreterInterface, ctx: FnCallContext) = when (snd.value) {
            0.0  -> Nil(ctx.position, reason="Division by zero")
            else -> Real(fst.value / snd.value, noPos)
        }
    }

    object Less : NumericOperatorBase("<", Types.Bool) {
        override fun calcIntegral(fst: Integral, snd: Integral, interpreter: InterpreterInterface, ctx: FnCallContext) = when {
            fst.value < snd.value -> Values.True
            else -> Values.False
        }

        override fun calcReal(fst: Real, snd: Real, interpreter: InterpreterInterface, ctx: FnCallContext) = when {
            fst.value < snd.value -> Values.True
            else -> Values.False
        }
    }

    object Greater : NumericOperatorBase(">", Types.Bool) {
        override fun calcIntegral(fst: Integral, snd: Integral, interpreter: InterpreterInterface, ctx: FnCallContext) = when {
            fst.value > snd.value -> Values.True
            else -> Values.False
        }

        override fun calcReal(fst: Real, snd: Real, interpreter: InterpreterInterface, ctx: FnCallContext) = when {
            fst.value > snd.value -> Values.True
            else -> Values.False
        }
    }

}

















