package org.fpeterek.tilscript.interpreter.interpreter.builtins

import org.fpeterek.tilscript.interpreter.interpreter.interpreterinterface.EagerFunction
import org.fpeterek.tilscript.interpreter.interpreter.interpreterinterface.FnCallContext
import org.fpeterek.tilscript.interpreter.interpreter.interpreterinterface.InterpreterInterface
import org.fpeterek.tilscript.interpreter.sentence.*
import org.fpeterek.tilscript.interpreter.util.SrcPosition

object TimeFunctions {

    object Now : EagerFunction(
        "Now",
        Types.Time,
        listOf(
        ),
    ) {
        override fun apply(interpreter: InterpreterInterface, args: List<Construction>, ctx: FnCallContext) =
            Timestamp(time=System.currentTimeMillis(), srcPos = ctx.position)
    }

    abstract class TimeCmp(name: String) : EagerFunction(
        name,
        Types.Bool,
        listOf(
            Variable("fst", srcPos = SrcPosition(-1, -1), type = Types.Time),
            Variable("snd", srcPos = SrcPosition(-1, -1), type = Types.Time)
        ),
    ) {

        override fun apply(interpreter: InterpreterInterface, args: List<Construction>, ctx: FnCallContext): Construction {
            val fst = args[0]
            val snd = args[1]

            if (fst !is Timestamp || snd !is Timestamp) {
                return Nil(reason="Symbolic values cannot be compared", srcPos = ctx.position)
            }

            return Bool(value = cmp(fst, snd), srcPos = ctx.position)
        }

        abstract fun cmp(t1: Timestamp, t2: Timestamp): Boolean

    }

    object IsBefore : TimeCmp("IsBefore") {
        override fun cmp(t1: Timestamp, t2: Timestamp) = t1.time < t2.time
    }

    object IsBeforeOrEq : TimeCmp("IsBeforeOrEq") {
        override fun cmp(t1: Timestamp, t2: Timestamp) = t1.time <= t2.time
    }

    object IsAfter : TimeCmp("IsAfter") {
        override fun cmp(t1: Timestamp, t2: Timestamp) = t1.time > t2.time
    }

    object IsAfterOrEq : TimeCmp("IsAfterOrEq") {
        override fun cmp(t1: Timestamp, t2: Timestamp) = t1.time >= t2.time
    }

}