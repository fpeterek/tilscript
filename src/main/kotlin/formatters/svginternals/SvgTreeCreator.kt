package org.fpeterek.til.typechecking.formatters.svginternals

import org.fpeterek.til.typechecking.greek.GreekAlphabet
import org.fpeterek.til.typechecking.sentence.*
import org.fpeterek.til.typechecking.types.Unknown

class SvgTreeCreator {

    companion object {
        fun createTree(cons: Construction): SentencePart = SvgTreeCreator().formTree(cons)
    }

    private var charOffset = 0

    private fun formatClosureVar(variable: Variable): String = when (variable.constructedType) {
        is Unknown -> variable.name
        else -> "${variable.name}: ${variable.constructedType.name}"
    }

    private fun convertValue(variable: Variable) = Value(
        variable.name,
        typename = variable.constructedType.name,
        leftOffset = charOffset,
    ).apply {
        charOffset += toString().length
    }

    private fun convertValue(triv: Trivialization) = Value(
        when (triv.construction) {
            is Literal     -> "'${triv.construction.value}"
            is TilFunction -> "'${triv.construction.name}"
            else           -> throw RuntimeException("Invalid state")
        },
        typename = triv.constructedType.name,
        leftOffset = charOffset,
    ).apply {
        charOffset += toString().length
    }

    private fun convertComposite(triv: Trivialization): SentencePart {

        val origOffset = charOffset
        charOffset += 1

        val comp = Composite(
            data = triv.construction.toString(),
            prefix = "'",
            suffix = "",
            treeData = formTree(triv.construction),
            typename = triv.constructedType.name,
            leftOffset = origOffset,
        )

        return comp
    }

    private fun convertComposite(exec: Execution): SentencePart {

        val prefix = exec.executionOrder.toString()

        val origOffset = charOffset
        charOffset += prefix.length

        val comp = Composite(
            data = exec.construction.toString(),
            prefix = exec.executionOrder.toString(),
            suffix = "",
            treeData = formTree(exec.construction),
            typename = exec.constructedType.name,
            leftOffset = origOffset,
        )

        return comp
    }

    private fun convertComposite(closure: Closure): SentencePart {

        val prefix = "\\" +
                closure.variables.joinToString(separator=" ", postfix=" ") { formatClosureVar(it) }

        val origOffset = charOffset
        charOffset += prefix.length

        val comp = Composite(
            data = closure.construction.toString(),
            prefix = prefix,
            suffix = "",
            treeData = formTree(closure.construction),
            typename = closure.constructedType.name,
            leftOffset = origOffset,
        )

        return comp
    }

    private fun convertCompArgs(comp: Composition): List<SentencePart> {

        val fn = formTree(comp.function)

        val args = comp.args.map {
            charOffset += 1  // Space left of each argument
            formTree(it)
        }

        return listOf(fn) + args
    }

    private fun convertComposition(comp: Composition): SentencePart {

        val origOffset = charOffset
        charOffset += 1  // Prefix

        val res = TilComposition(
            data = comp.tsString().drop(1).dropLast(1),
            args = convertCompArgs(comp),
            typename = comp.constructedType.name,
            leftOffset = origOffset,
        )

        charOffset += 1  // Suffix

        return res
    }

    fun formTree(cons: Construction): SentencePart = when (cons) {
        is Closure -> convertComposite(cons)
        is Composition -> convertComposition(cons)
        is Execution -> convertComposite(cons)
        is Literal, is TilFunction -> throw RuntimeException("Invalid state")
        is Trivialization -> when (cons.construction) {
            is Literal, is TilFunction -> convertValue(cons)
            else -> convertComposite(cons)
        }
        is Variable -> convertValue(cons)
    }

}
