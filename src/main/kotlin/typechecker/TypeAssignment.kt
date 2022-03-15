package org.fpeterek.til.typechecking.typechecker

import org.fpeterek.til.typechecking.constructions.*
import org.fpeterek.til.typechecking.types.Type
import org.fpeterek.til.typechecking.util.Util.incrementOrder

object TypeAssignment {

    /* Variables, trivializations and executions increase order */
    /* Compositions and closures retain order                   */

    fun Variable.assignType(type: Type) =
        Variable(this.name, type, type.incrementOrder())

    fun Trivialization.assignType() =
        Trivialization(construction, construction.constructionType.incrementOrder())

    fun Execution.assignType() =
        Execution(construction, executionOrder, construction.constructionType.incrementOrder())

    fun Closure.assignType(variables: List<Variable>, construction: Construction) =
        Closure(variables, construction, construction.constructionType)
}
