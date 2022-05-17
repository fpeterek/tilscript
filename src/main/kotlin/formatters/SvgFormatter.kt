package org.fpeterek.til.typechecking.formatters

import org.fpeterek.til.typechecking.sentence.*
import org.fpeterek.til.typechecking.types.Unknown

class SvgFormatter private constructor(private val construction: Construction) {

    companion object {
        fun format(constructions: List<Construction>): String = SvgFormatter(constructions.first()).formSvg()

        private fun formTree(cons: Construction): StringPart = when (cons) {
            is Closure -> Composite(cons)
            is Composition -> TilComposition(cons)
            is Execution -> Composite(cons)
            is Literal, is TilFunction -> throw RuntimeException("Invalid state")
            is Trivialization -> when (cons.construction) {
                is Literal, is TilFunction -> Value(cons)
                else -> Composite(cons)
            }
            is Variable -> Value(cons)
        }
    }

    private sealed class StringPart {
        abstract val prefix: String
        abstract val data: String
        abstract val suffix: String

        val length get() = toString().length

        override fun toString() = "$prefix$data$suffix"
    }

    private class Value(override val data: String) : StringPart() {
        override val prefix get() = ""
        override val suffix get() = ""

        constructor(variable: Variable) : this(variable.name)

        constructor(triv: Trivialization) : this(
            when (triv.construction) {
                is Literal     -> "'${triv.construction.value}"
                is TilFunction -> "'${triv.construction.name}"
                else           -> throw RuntimeException("Invalid state")
            }
        )
    }

    private class Composite(
        override val data: String,
        override val prefix: String,
        override val suffix: String,
        val treeData: StringPart) : StringPart() {

        companion object {
            private fun formatClosureVar(variable: Variable): String = when (variable.constructedType) {
                is Unknown -> variable.name
                else -> "${variable.name}: ${variable.constructedType}"
            }
        }

        constructor(triv: Trivialization) : this(
            data = triv.construction.toString(),
            prefix = "'",
            suffix = "",
            treeData = formTree(triv.construction)
        )

        constructor(exec: Execution) : this(
            data = exec.construction.toString(),
            prefix = exec.executionOrder.toString(),
            suffix = "",
            treeData = formTree(exec.construction)
        )

        constructor(closure: Closure) : this(
            data = closure.construction.toString(),
            prefix = "\\${closure.variables.joinToString(separator=" ", postfix=" ") { formatClosureVar(it) }}",
            suffix = "",
            treeData = formTree(closure.construction)
        )
    }

    private class TilComposition(comp: Composition) : StringPart() {
        override val prefix = "["
        override val data = comp.tsString().drop(1).dropLast(1)
        override val suffix = "]"

        val args = listOf(comp.function) + comp.args
    }

    private fun formTree() = formTree(construction)

    private val constructionString = construction.toString()
    private val tree = formTree()

    private fun traverse(tree: StringPart, depth: Int = 0, leftPadding: Int = 20): Nothing = when (tree) {
        is Composite -> TODO()
        is TilComposition -> TODO()
        is Value -> TODO()
    }

    private val builder = StringBuilder()
        .append("""<?xml version="1.0" standalone="no"?>""")
        .append("""<!DOCTYPE svg PUBLIC "-//W3C//DTD SVG 1.1//EN" 
                     |"http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd">""".trimMargin())
        .append("""<svg version="1.1" xmlns="http://www.w3.org/2000/svg">""")
        .append("""<text x="20" y="30" font-size="18" font-family="Inconsolata, monospace">
                |${constructionString}</text>""".trimMargin())

    private fun formSvg(): String = builder.append("</svg>").toString()

}
