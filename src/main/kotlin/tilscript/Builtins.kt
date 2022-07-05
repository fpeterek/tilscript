package org.fpeterek.til.typechecking.tilscript

import org.fpeterek.til.typechecking.sentence.Bool
import org.fpeterek.til.typechecking.sentence.TilFunction
import org.fpeterek.til.typechecking.types.*
import org.fpeterek.til.typechecking.util.SrcPosition

object Builtins {

    val Bool = AtomicType("Bool", "Truth values")
    val Indiv = AtomicType("Indiv", "Individuals")
    val Time = AtomicType("Time", "Timestamps")
    val World = AtomicType("World", "Worlds")
    val Real = AtomicType("Real", "Real numbers")
    val Int = AtomicType("Int", "Whole numbers")

    val builtinTypes = listOf(
        Bool, Indiv, Time, World, Real, Int
    )

    private val realOperation
        get() = FunctionType(Real, Real, Real)

    private val intOperation
        get() = FunctionType(Int, Int, Int)

    private val equalityComparison
        get() = FunctionType(Bool, GenericType(1), GenericType(1))

    private val nonrestrictedQuantifier
        get() = FunctionType(Bool, FunctionType(Bool, GenericType(1)))

    private val restrictedQuantifier
        get() = FunctionType(FunctionType(Bool, GenericType(1)), FunctionType(Bool, GenericType(1)))

    private val binaryBoolean
        get() = FunctionType(Bool, Bool, Bool)
    private val unaryBoolean
        get() = FunctionType(Bool, Bool)

    private val constructionTruthiness
        get() = FunctionType(Bool, ConstructionType)
    private val propositionTruthiness
        get() = FunctionType(Bool, CommonTypes.proposition)

    private val singularizer
        get() = FunctionType(GenericType(1), FunctionType(Bool, GenericType(1)))

    private val noPosition = SrcPosition(-1, -1)

    val builtinFunctions = listOf(
        // TODO: Idk whether integer promotion is supported in TILScript
        TilFunction("+", noPosition, realOperation),
        TilFunction("-", noPosition, realOperation),
        TilFunction("*", noPosition, realOperation),
        TilFunction("/", noPosition, realOperation),

        // TilFunction("+", intOperation),
        // TilFunction("-", intOperation),
        // TilFunction("*", intOperation),
        // TilFunction("/", intOperation),

        TilFunction("=", noPosition, equalityComparison),

        TilFunction("ToInt", noPosition, FunctionType(Int, Real)),
        TilFunction("ToReal", noPosition, FunctionType(Real, Int)),

        TilFunction("ForAll", noPosition, nonrestrictedQuantifier),
        TilFunction("Exist", noPosition, nonrestrictedQuantifier),
        TilFunction("Sing", noPosition, singularizer),

        TilFunction("Every", noPosition, restrictedQuantifier),
        TilFunction("Some", noPosition, restrictedQuantifier),
        TilFunction("No", noPosition, restrictedQuantifier),

        TilFunction("And", noPosition, binaryBoolean),
        TilFunction("Or", noPosition, binaryBoolean),
        TilFunction("Implies", noPosition, binaryBoolean),
        TilFunction("Not", noPosition, unaryBoolean),

        TilFunction("Sub", noPosition, FunctionType(ConstructionType, ConstructionType, ConstructionType, ConstructionType)),
        TilFunction("Tr", noPosition, FunctionType(ConstructionType, ConstructionType)),

        TilFunction("TrueC", noPosition, constructionTruthiness),
        TilFunction("FalseC", noPosition, constructionTruthiness),
        TilFunction("ImproperC", noPosition, constructionTruthiness),

        TilFunction("TrueP", noPosition, propositionTruthiness),
        TilFunction("FalseP", noPosition, propositionTruthiness),
        TilFunction("UndefP", noPosition, propositionTruthiness),
    )

    val True = Bool(true, noPosition)
    val False = Bool(false, noPosition)

    val builtinValues = listOf(True, False)

}