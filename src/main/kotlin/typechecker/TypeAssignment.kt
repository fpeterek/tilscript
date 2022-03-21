package org.fpeterek.til.typechecking.typechecker

import org.fpeterek.til.typechecking.constructions.*
import org.fpeterek.til.typechecking.types.ConstructionType
import org.fpeterek.til.typechecking.types.FunctionType
import org.fpeterek.til.typechecking.types.Type
import org.fpeterek.til.typechecking.util.Util.incrementOrder

object TypeAssignment {

    /* Variables, trivializations and executions increase order */
    /* Compositions and closures retain order                   */

    fun Variable.assignType(type: Type) =
        Variable(this.name, type, type.incrementOrder())

    fun Trivialization.assignType(type: Type) = Trivialization(
        construction=construction,
        constructedType=type,
        constructionType=construction.constructionType.incrementOrder()
    )

    fun Execution.assignType() =
        Execution(construction, executionOrder, construction.constructionType.incrementOrder())

    fun Closure.assignType(variables: List<Variable>, construction: Construction) =
        Closure(variables, construction, construction.constructionType)

    fun TilFunction.assignType(type: FunctionType) =
        TilFunction(name, type)
}
