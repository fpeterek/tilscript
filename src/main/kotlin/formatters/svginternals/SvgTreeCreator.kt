package org.fpeterek.til.typechecking.formatters.svginternals

import org.fpeterek.til.typechecking.sentence.*
import org.fpeterek.til.typechecking.types.Unknown

class SvgTreeCreator {

    companion object {
        fun createTree(cons: Construction): SentencePart = SvgTreeCreator().formTree(cons)
    }

    private fun formatClosureVar(variable: Variable): String = when (variable.constructedType) {
        is Unknown -> variable.name
        else -> "${variable.name}: ${variable.constructedType}"
    }

    private fun convertValue(variable: Variable) = Value(
        variable.name,
        typename = variable.constructedType.name,
    )

    private fun convertValue(triv: Trivialization) = Value(
        when (triv.construction) {
            is Literal     -> "'${triv.construction.value}"
            is TilFunction -> "'${triv.construction.name}"
            else           -> throw RuntimeException("Invalid state")
        },
        typename = triv.constructedType.name,
    )

    private fun convertComposite(triv: Trivialization) = Composite(
        data = triv.construction.toString(),
        prefix = "'",
        suffix = "",
        treeData = formTree(triv.construction),
        typename = triv.constructedType.name,
    )

    private fun convertComposite(exec: Execution) = Composite(
        data = exec.construction.toString(),
        prefix = exec.executionOrder.toString(),
        suffix = "",
        treeData = formTree(exec.construction),
        typename = exec.constructedType.name,
    )

    private fun convertComposite(closure: Closure) = Composite(
        data = closure.construction.toString(),
        prefix = "\\${closure.variables.joinToString(separator=" ", postfix=" ") { formatClosureVar(it) }}",
        suffix = "",
        treeData = formTree(closure.construction),
        typename = closure.constructedType.name,
    )

    private fun convertComposite(comp: Composition) = TilComposition(
        data = comp.tsString().drop(1).dropLast(1),
        args = listOf(formTree(comp.function)) + comp.args.map(::formTree),
        typename = comp.constructedType.name,
    )

    fun formTree(cons: Construction): SentencePart = when (cons) {
        is Closure -> convertComposite(cons)
        is Composition -> convertComposite(cons)
        is Execution -> convertComposite(cons)
        is Literal, is TilFunction -> throw RuntimeException("Invalid state")
        is Trivialization -> when (cons.construction) {
            is Literal, is TilFunction -> convertValue(cons)
            else -> convertComposite(cons)
        }
        is Variable -> convertValue(cons)
    }

}
