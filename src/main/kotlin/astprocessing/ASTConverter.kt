package org.fpeterek.til.typechecking.astprocessing

import org.fpeterek.til.typechecking.astprocessing.result.*
import org.fpeterek.til.typechecking.astprocessing.result.Construction.*
import org.fpeterek.til.typechecking.sentence.*
import org.fpeterek.til.typechecking.types.AtomicType
import org.fpeterek.til.typechecking.types.FunctionType
import org.fpeterek.til.typechecking.types.Type
import org.fpeterek.til.typechecking.types.TypeRepository
import org.fpeterek.til.typechecking.types.TypeAlias as TilTypeAlias
import org.fpeterek.til.typechecking.sentence.Construction as TilConstruction
import org.fpeterek.til.typechecking.sentence.Execution as TilExecution
import org.fpeterek.til.typechecking.sentence.Composition as TilComposition
import org.fpeterek.til.typechecking.sentence.Closure as TilClosure
import org.fpeterek.til.typechecking.sentence.Variable as TilVariable
import org.fpeterek.til.typechecking.sentence.Trivialization as TilTrivialization


class ASTConverter private constructor() {

    companion object {
        fun convert(sentences: Sentences) = ASTConverter().convert(sentences)
    }

    private val lits = mutableSetOf<String>()
    private val fns = mutableSetOf<String>()

    private val repo = TypeRepository()

    private fun convert(sentences: Sentences) {
        sentences.sentences.map(::convertSentence)
    }

    private fun convertSentence(sentence: IntermediateResult) = when (sentence) {
        is Construction -> convertConstruction(sentence)
        is GlobalVarDef -> convertGlobalVarDef(sentence)
        is EntityDef    -> convertLiteralDef(sentence)
        is TypeAlias    -> convertTypeAlias(sentence)

        else -> throw RuntimeException("Invalid parser state")
    }

    private fun convertConstruction(construction: Construction): TilConstruction = when (construction) {
        is Closure     -> convertClosure(construction)
        is Composition -> convertComposition(construction)
        is Execution   -> convertExecution(construction)
        is VarRef      -> convertVarRef(construction)
    }

    private fun convertGlobalVarDef(def: GlobalVarDef) = VariableDefinition(
        def.vars.map { it.toString() },
        convertDataType(def.type)
    )

    private fun convertLiteralDef(entityDef: EntityDef) = LiteralDefinition(
        entityDef.names,
        convertDataType(entityDef.type)
    ).apply {
        when (type) {
            is FunctionType -> fns.addAll(names)
            else -> lits.addAll(names)
        }
    }

    private fun convertTypeAlias(typeAlias: TypeAlias) =
        TypeDefinition(
            TilTypeAlias(
                shortName="",
                name=typeAlias.name,
                type=convertDataType(typeAlias.type)
            ).let { repo.process(it) }
        )

    private fun convertClosure(closure: Closure): TilClosure = TilClosure(
        variables=closure.vars.map(::convertTypedVar),
        construction=convertConstruction(closure.construction)
    )

    private fun convertTypedVar(typedVar: TypedVar) = TilVariable(
        typedVar.name,
        repo[typedVar.name] ?: throw RuntimeException("Unknown type: ${typedVar.name}")
    )

    private fun convertComposition(composition: Composition) = TilComposition(
        function=convertConstruction(composition.fn),
        args=composition.args.map(::convertConstruction)
    )

    private fun convertExecution(execution: Execution): TilConstruction = when (execution.order) {
        0    -> convertTilTrivialization(execution)
        1, 2 -> convertTilExecution(execution)
        else -> throw RuntimeException("Invalid parser state")
    }

    private fun convertTilExecution(execution: Execution) = TilExecution(
        construction=when (execution.construction) {
            is Construction -> convertConstruction(execution.construction)
            else -> throw RuntimeException("Invalid parser state")
        },
        executionOrder=execution.order
    )

    private fun convertTilTrivialization(execution: Execution) = TilTrivialization(
        construction=when (execution.construction) {
            is Construction -> convertConstruction(execution.construction)
            is Entity -> convertEntityRef(execution.construction)
            else -> throw RuntimeException("Invalid parser state")
        }
    )

    private fun convertEntityRef(entity: Entity): TilConstruction = when (entity) {
        is Entity.Number -> Literal(entity.value, AtomicType.Tau)
        is Entity.FnOrEntity -> when (entity.value) {
            in fns -> TilFunction(entity.value)
            else -> Literal(entity.value)
        }
    }

    private fun convertVarRef(varRef: VarRef): TilVariable = TilVariable(varRef.name)

    // TODO: Finish type conversion
    private fun convertDataType(type: DataType): Type = when (type) {
        is DataType.ClassType -> convertFnType(type)
        is DataType.Collection.List -> TODO()
        is DataType.Collection.Tuple -> TODO()
        is DataType.PrimitiveType -> TODO()
    }

    private fun convertFnType(classType: DataType.ClassType) = FunctionType(
        classType.signature.map { convertDataType(it) }
    )

}
