package org.fpeterek.til.typechecking.tilscript

import org.fpeterek.til.typechecking.greek.GreekAlphabet
import org.fpeterek.til.typechecking.sentence.TilFunction
import org.fpeterek.til.typechecking.types.*

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


    // TODO: Built-in literals (true/false)

    val builtinFunctions = listOf(
        // TODO: Function overloading
        //       Or integer promotion
        TilFunction("+", realOperation),
        TilFunction("-", realOperation),
        TilFunction("*", realOperation),
        TilFunction("/", realOperation),

//        TilFunction("+", intOperation),
//        TilFunction("-", intOperation),
//        TilFunction("*", intOperation),
//        TilFunction("/", intOperation),

        TilFunction("=", equalityComparison),

        TilFunction("ToInt", FunctionType(Nu, Eta)),
        TilFunction("ToReal", FunctionType(Eta, Nu)),

        TilFunction("ForAll", nonrestrictedQuantifier),
        TilFunction("Exist", nonrestrictedQuantifier),
        TilFunction("Sing", singularizer),

        TilFunction("Every", restrictedQuantifier),
        TilFunction("Some", restrictedQuantifier),
        TilFunction("No", restrictedQuantifier),

        TilFunction("And", binaryBoolean),
        TilFunction("Or", binaryBoolean),
        TilFunction("Implies", binaryBoolean),
        TilFunction("Not", unaryBoolean),

        TilFunction("Sub", FunctionType(ConstructionType, ConstructionType, ConstructionType, ConstructionType)),
        TilFunction("Tr", FunctionType(ConstructionType, ConstructionType)),

        TilFunction("TrueC", constructionTruthiness),
        TilFunction("FalseC", constructionTruthiness),
        TilFunction("ImproperC", constructionTruthiness),

        TilFunction("TrueP", propositionTruthiness),
        TilFunction("FalseP", propositionTruthiness),
        TilFunction("UndefP", propositionTruthiness),
    )

}