package org.fpeterek.til.typechecking.tilscript

import org.fpeterek.til.typechecking.greek.GreekAlphabet
import org.fpeterek.til.typechecking.sentence.Bool
import org.fpeterek.til.typechecking.sentence.Literal
import org.fpeterek.til.typechecking.sentence.TilFunction
import org.fpeterek.til.typechecking.types.*
import org.fpeterek.til.typechecking.util.SrcPosition

object Builtins {

    val Omicron = AtomicType(GreekAlphabet.omicron, "Bool", "Truth values")
    val Iota = AtomicType(GreekAlphabet.iota, "Indiv", "Individuals")
    val Tau = AtomicType(GreekAlphabet.tau, "Time", "Timestamps")
    val Omega = AtomicType(GreekAlphabet.omega, "World", "Worlds")
    val Eta = AtomicType(GreekAlphabet.eta, "Real", "Real numbers")
    val Nu = AtomicType(GreekAlphabet.nu, "Int", "Whole numbers")

    val builtinTypes = listOf(
        Omicron, Iota, Tau, Omega, Eta, Nu
    )

    private val realOperation
        get() = FunctionType(Eta, Eta, Eta)

    private val intOperation
        get() = FunctionType(Nu, Nu, Nu)

    private val equalityComparison
        get() = FunctionType(Omicron, GenericType(1), GenericType(1))

    private val nonrestrictedQuantifier
        get() = FunctionType(Omicron, FunctionType(Omicron, GenericType(1)))

    private val restrictedQuantifier
        get() = FunctionType(FunctionType(Omicron, GenericType(1)), FunctionType(Omicron, GenericType(1)))

    private val binaryBoolean
        get() = FunctionType(Omicron, Omicron, Omicron)
    private val unaryBoolean
        get() = FunctionType(Omicron, Omicron)

    private val constructionTruthiness
        get() = FunctionType(Omicron, ConstructionType)
    private val propositionTruthiness
        get() = FunctionType(Omicron, CommonTypes.proposition)

    private val singularizer
        get() = FunctionType(GenericType(1), FunctionType(Omicron, GenericType(1)))

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

        TilFunction("ToInt", noPosition, FunctionType(Nu, Eta)),
        TilFunction("ToReal", noPosition, FunctionType(Eta, Nu)),

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