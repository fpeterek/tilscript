package org.fpeterek.til.typechecking.typechecker

import org.fpeterek.til.typechecking.sentence.*
import org.fpeterek.til.typechecking.types.ConstructionType
import org.fpeterek.til.typechecking.types.FunctionType
import org.fpeterek.til.typechecking.types.Type
import org.fpeterek.til.typechecking.types.Unknown

object TypeAssignment {

    /* Variables, trivializations and executions increase order */
    /* Compositions and closures retain order                   */

    fun Variable.assignType(type: Type) =
        Variable(this.name, type)

    fun Trivialization.assignType(type: Type) = Trivialization(
        construction=construction,
        constructedType=type,
        constructionType=ConstructionType
    )

    fun Execution.assignType() =
        Execution(construction, executionOrder, ConstructionType)

    fun Closure.assignType() = Closure(
        variables,
        construction,
        FunctionType(imageType=construction.constructedType, argTypes=variables.map { it.constructedType }),
        constructionType
    )

    private fun Closure.performAssignment(vars: List<Type>) = Closure(
        variables.zip(vars).map { (orig, type) -> Variable(orig.name, type) },
        construction,
        Unknown,
        constructionType
    )

    fun Closure.assignType(vars: List<Type>) = if (vars.size != variables.size) {
        throw RuntimeException("Function arity mismatch")
    } else {
        performAssignment(vars)
    }


    fun Composition.assignType(type: Type) = Composition(
        function, args, type
    )

    fun Literal.assignType(type: Type) = Literal(value, type)

    fun TilFunction.assignType(type: FunctionType) =
        TilFunction(name, type)
}
