package org.fpeterek.tilscript.math

import org.fpeterek.tilscript.common.SrcPosition
import org.fpeterek.tilscript.common.interpreterinterface.DefaultFunction
import org.fpeterek.tilscript.common.interpreterinterface.FnCallContext
import org.fpeterek.tilscript.common.interpreterinterface.InterpreterInterface
import org.fpeterek.tilscript.common.sentence.*
import org.fpeterek.tilscript.common.types.Primitives
import kotlin.math.*

object Sin : DefaultFunction(
    "Sin",
    Primitives.Real,
    listOf(
        Variable("x", SrcPosition(-1, -1), Primitives.Real)
    )
) {

    override fun apply(interpreter: InterpreterInterface, args: List<Construction>, ctx: FnCallContext): Construction {

        val x = args[0]

        if (x is Symbol && x.value == "Pi" && interpreter.typesMatch(x.constructionType, Primitives.Real)) {
            return Real(value = 0.0, srcPos = ctx.position)
        }

        if (x is Symbol) {
            return Nil(ctx.position, reason="Cannot compute the sine of a symbolic value")
        }

        x as Real

        return Real(sin(x.value), ctx.position)
    }

}

object Asin : DefaultFunction(
    "Asin",
    Primitives.Real,
    listOf(
        Variable("x", SrcPosition(-1, -1), Primitives.Real)
    )
) {

    override fun apply(interpreter: InterpreterInterface, args: List<Construction>, ctx: FnCallContext): Construction {

        val x = args[0]

        if (x is Symbol) {
            return Nil(ctx.position, reason="Cannot compute asin of a symbolic value")
        }

        x as Real

        return Real(asin(x.value), ctx.position)
    }

}

object Cos : DefaultFunction(
    "Cos",
    Primitives.Real,
    listOf(
        Variable("x", SrcPosition(-1, -1), Primitives.Real)
    )
) {

    override fun apply(interpreter: InterpreterInterface, args: List<Construction>, ctx: FnCallContext): Construction {

        val x = args[0]

        if (x is Symbol && x.value == "Pi" && interpreter.typesMatch(x.constructionType, Primitives.Real)) {
            return Real(value = -1.0, srcPos = ctx.position)
        }

        if (x is Symbol) {
            return Nil(ctx.position, reason="Cannot compute the cosine of a symbolic value")
        }

        x as Real

        return Real(cos(x.value), ctx.position)
    }

}

object Acos : DefaultFunction(
    "Acos",
    Primitives.Real,
    listOf(
        Variable("x", SrcPosition(-1, -1), Primitives.Real)
    )
) {

    override fun apply(interpreter: InterpreterInterface, args: List<Construction>, ctx: FnCallContext): Construction {

        val x = args[0]

        if (x is Symbol) {
            return Nil(ctx.position, reason="Cannot compute acos of a symbolic value")
        }

        x as Real

        return Real(acos(x.value), ctx.position)
    }

}

object Tan : DefaultFunction(
    "Tan",
    Primitives.Real,
    listOf(
        Variable("x", SrcPosition(-1, -1), Primitives.Real)
    )
) {

    override fun apply(interpreter: InterpreterInterface, args: List<Construction>, ctx: FnCallContext): Construction {

        val x = args[0]

        if (x is Symbol && x.value == "Pi" && interpreter.typesMatch(x.constructionType, Primitives.Real)) {
            return Real(value = 0.0, srcPos = ctx.position)
        }

        if (x is Symbol) {
            return Nil(ctx.position, reason="Cannot compute the tangent of a symbolic value")
        }

        x as Real

        return try {
            Real(tan(x.value), ctx.position)
        } catch (e: Exception) {
            Nil(ctx.position, reason="Tangent is undefined on argument ${x.value}")
        }
    }

}

object Ln : DefaultFunction(
    "Ln",
    Primitives.Real,
    listOf(
        Variable("x", SrcPosition(-1, -1), Primitives.Real)
    )
) {

    override fun apply(interpreter: InterpreterInterface, args: List<Construction>, ctx: FnCallContext): Construction {

        val x = args[0]

        if (x is Symbol && x.value == "E" && interpreter.typesMatch(x.constructionType, Primitives.Real)) {
            return Real(value = 1.0, srcPos = ctx.position)
        }

        if (x is Symbol) {
            return Nil(ctx.position, reason="Cannot compute the logarithm of a symbolic value")
        }

        x as Real

        return try {
            Real(ln(x.value), ctx.position)
        } catch (e: Exception) {
            Nil(ctx.position, reason="Logarithm is undefined on argument ${x.value}")
        }
    }

}

object Log : DefaultFunction(
    "Log",
    Primitives.Real,
    listOf(
        Variable("x", SrcPosition(-1, -1), Primitives.Real),
        Variable("base", SrcPosition(-1, -1), Primitives.Real),
    )
) {

    override fun apply(interpreter: InterpreterInterface, args: List<Construction>, ctx: FnCallContext): Construction {

        val x = args[0]
        val base = args[1]

        if (x !is Real) {
            return Nil(ctx.position, reason="Cannot compute the logarithm of a symbolic value")
        }

        if (base !is Real) {
            return Nil(ctx.position, reason="Logarithm base must not be a symbolic value")
        }

        return try {
            Real(log(x.value, base=base.value), ctx.position)
        } catch (e: Exception) {
            Nil(ctx.position, reason="Logarithm is undefined on argument ${x.value}")
        }
    }

}

object Log10 : DefaultFunction(
    "Log10",
    Primitives.Real,
    listOf(
        Variable("x", SrcPosition(-1, -1), Primitives.Real),
    )
) {

    override fun apply(interpreter: InterpreterInterface, args: List<Construction>, ctx: FnCallContext) =
        Log(interpreter, args + listOf(Real(10.0, SrcPosition(-1, -1))), ctx)
}

object Log2 : DefaultFunction(
    "Log2",
    Primitives.Real,
    listOf(
        Variable("x", SrcPosition(-1, -1), Primitives.Real),
    )
) {

    override fun apply(interpreter: InterpreterInterface, args: List<Construction>, ctx: FnCallContext) =
        Log(interpreter, args + listOf(Real(2.0, SrcPosition(-1, -1))), ctx)
}

object Round : DefaultFunction(
    "Round",
    Primitives.Real,
    listOf(
        Variable("x", SrcPosition(-1, -1), Primitives.Real)
    )
) {

    override fun apply(interpreter: InterpreterInterface, args: List<Construction>, ctx: FnCallContext): Construction {

        val x = args[0]

        if (x !is Real) {
            return Nil(ctx.position, reason="Cannot round a symbolic value")
        }

        return Real(round(x.value), ctx.position)
    }

}

object Sqrt : DefaultFunction(
    "Sqrt",
    Primitives.Real,
    listOf(
        Variable("x", SrcPosition(-1, -1), Primitives.Real)
    )
) {

    override fun apply(interpreter: InterpreterInterface, args: List<Construction>, ctx: FnCallContext): Construction {

        val x = args[0]

        if (x !is Real) {
            return Nil(ctx.position, reason="Cannot round a symbolic value")
        }

        if (x.value < 0) {
            return Nil(ctx.position, reason="Sqrt is undefined for values lower than zero")
        }

        return Real(sqrt(x.value), ctx.position)
    }

}
