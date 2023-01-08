package org.fpeterek.tilscript.common.interpreterinterface

import org.fpeterek.tilscript.common.die
import org.fpeterek.tilscript.common.sentence.Construction
import org.fpeterek.tilscript.common.sentence.Variable
import org.fpeterek.tilscript.common.types.Type

// This abstract class marks functions which make absolutely no assumptions about its arguments
// and delegates all handling to the programmer
// The user can never be presented with this interface as they do not deserve to wield such power
abstract class BuiltinUncheckedFunction(
    name: String,
    returns: Type,
    args: List<Variable>,
    override val acceptsNil: Boolean
): FunctionInterface(name, returns, args) {

    override fun invoke(interpreter: InterpreterInterface, args: List<Construction>, ctx: FnCallContext): Construction {
        if (args.size != this.args.size) {
            die("Function $name expects ${this.args.size} arguments (received ${args.size})")
        }
        return apply(interpreter, args, ctx)
    }

}