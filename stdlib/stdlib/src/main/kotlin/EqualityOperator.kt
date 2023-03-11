package org.fpeterek.tilscript.stdlib

import org.fpeterek.tilscript.common.interpreterinterface.LambdaFunction
import org.fpeterek.tilscript.common.interpreterinterface.OperatorFunction
import org.fpeterek.tilscript.common.interpreterinterface.FnCallContext
import org.fpeterek.tilscript.common.interpreterinterface.InterpreterInterface
import org.fpeterek.tilscript.common.sentence.*
import org.fpeterek.tilscript.common.types.GenericType
import org.fpeterek.tilscript.common.types.Type
import org.fpeterek.tilscript.common.SrcPosition


object EqualityOperator : OperatorFunction(
    "=",
    Types.Bool,
    listOf(
        Variable("fst", SrcPosition(-1, -1), GenericType(1)),
        Variable("snd", SrcPosition(-1, -1), GenericType(1)),
    )
) {

    private class ConstructionComparator(private val interpreter: InterpreterInterface) {

        companion object {
            operator fun invoke(fst: Construction, snd: Construction, interpreter: InterpreterInterface) =
                ConstructionComparator(interpreter).apply(fst, snd)

        }

        private infix fun Type.matches(other: Type) =
            interpreter.typesMatch(this, other)

        private infix fun Bool.eq(other: Bool) =
            value == other.value

        private infix fun Integral.eq(other: Integral) =
            value == other.value

        private infix fun Real.eq(other: Real) =
            value == other.value

        private infix fun Symbol.eq(other: Symbol) =
            value == other.value && constructionType matches other.constructionType

        private infix fun Text.eq(other: Text) =
            value == other.value

        private infix fun ListCell.eq(other: ListCell): Boolean =
            head eq other.head && tail eq other.tail

        private infix fun TilList.eq(other: TilList): Boolean = when {
            !(valueType matches other.valueType)    -> false

            this is ListCell  && other is ListCell  -> this eq other
            this is EmptyList && other is EmptyList -> true

            else                                    -> false
        }

        private infix fun TilTuple.eq(other: TilTuple) =
            values.asSequence()
                .zip(other.values.asSequence())
                .all { (fst, snd) -> fst eq snd }

        private infix fun Timestamp.eq(other: Timestamp) =
            time == other.time

        private infix fun TypeRef.eq(other: TypeRef) =
            type matches other.type

        private infix fun Struct.eq(other: Struct) =
            structType matches other.structType and
                    this.attributes.zip(other.attributes).all { it.first eq it.second }

        private infix fun Value.eq(other: Value): Boolean = when (this) {
            is Bool        -> this eq (other as Bool)
            is Integral    -> this eq (other as Integral)
            is Nil         -> false
            is Real        -> this eq (other as Real)
            is Symbol      -> this eq (other as Symbol)
            is Text        -> this eq (other as Text)
            is EmptyList   -> this eq (other as EmptyList)
            is ListCell    -> this eq (other as ListCell)
            is TilTuple    -> this eq (other as TilTuple)
            is Timestamp   -> this eq (other as Timestamp)
            is TypeRef     -> this eq (other as TypeRef)
            is Struct      -> this eq (other as Struct)
            is World       -> true
            is DeviceState -> true
        }

        private infix fun Closure.eq(other: Closure) =
            variables.size == other.variables.size &&
            variables.asSequence().zip(other.variables.asSequence()).all { (fst, snd) -> fst eq snd } &&
            construction eq other.construction

        private infix fun Composition.eq(other: Composition) =
            args.size == other.args.size &&
            args.asSequence().zip(other.args.asSequence()).all { (fst, snd) -> fst eq snd }

        private infix fun Execution.eq(other: Execution) =
            executionOrder == other.executionOrder && construction eq other.construction

        private infix fun TilFunction.eq(other: TilFunction) = when {

            this.implementation != null && this.implementation is LambdaFunction &&
                other.implementation != null && other.implementation is LambdaFunction ->
                (this.implementation as LambdaFunction).body eq (other.implementation as LambdaFunction).body

            else -> name == other.name && constructedType matches other.constructedType
        }

        private infix fun Trivialization.eq(other: Trivialization) =
            construction eq other.construction

        private infix fun Variable.eq(other: Variable) =
            name == other.name && constructedType matches other.constructedType

        private infix fun Construction.eq(other: Construction): Boolean = when {
            this.javaClass != other.javaClass                 -> false

            this is Closure        && other is Closure        -> this eq other
            this is Composition    && other is Composition    -> this eq other
            this is Execution      && other is Execution      -> this eq other
            this is TilFunction    && other is TilFunction    -> this eq other
            this is Trivialization && other is Trivialization -> this eq other
            this is Value          && other is Value          -> this eq other
            this is Variable       && other is Variable       -> this eq other

            else -> false
        }

        fun apply(fst: Construction, snd: Construction) = fst eq snd

    }

    override fun apply(interpreter: InterpreterInterface, args: List<Construction>, ctx: FnCallContext) =
        Bool(value= ConstructionComparator(args[0], args[1], interpreter), srcPos = ctx.position)
}