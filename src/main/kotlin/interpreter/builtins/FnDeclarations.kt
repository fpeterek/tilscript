package org.fpeterek.til.interpreter.interpreter.builtins

import org.fpeterek.til.interpreter.sentence.TilFunction
import org.fpeterek.til.interpreter.tilscript.CommonTypes
import org.fpeterek.til.interpreter.types.*
import org.fpeterek.til.interpreter.util.SrcPosition

object FnDeclarations {

    private val realOperation
        get() = FunctionType(Types.Real, Types.Real, Types.Real)

    private val intOperation
        get() = FunctionType(Types.Int, Types.Int, Types.Int)

    private val equalityComparison
        get() = FunctionType(Types.Bool, GenericType(1), GenericType(1))

    private val nonrestrictedQuantifier
        get() = FunctionType(Types.Bool, FunctionType(Types.Bool, GenericType(1)))

    private val restrictedQuantifier
        get() = FunctionType(FunctionType(Types.Bool, GenericType(1)), FunctionType(Types.Bool, GenericType(1)))

    private val binaryBoolean
        get() = FunctionType(Types.Bool, Types.Bool, Types.Bool)
    private val unaryBoolean
        get() = FunctionType(Types.Bool, Types.Bool)

    private val constructionTruthiness
        get() = FunctionType(Types.Bool, ConstructionType)
    private val propositionTruthiness
        get() = FunctionType(Types.Bool, CommonTypes.proposition)

    private val singularizer
        get() = FunctionType(GenericType(1), FunctionType(Types.Bool, GenericType(1)))

    private val listCons
        get() = FunctionType(ListType(GenericType(1)), GenericType(1), ListType(GenericType(1)))

    private val noPosition = SrcPosition(-1, -1)

    val builtinFunctions = listOf(
        TilFunction("+", noPosition, realOperation),
        TilFunction("-", noPosition, realOperation),
        TilFunction("*", noPosition, realOperation),
        TilFunction("/", noPosition, realOperation),

        TilFunction("Cons", noPosition, listCons),

        TilFunction("=", noPosition, equalityComparison),

        TilFunction("ToTypes.Int", noPosition, FunctionType(Types.Int, Types.Real)),
        TilFunction("ToTypes.Real", noPosition, FunctionType(Types.Real, Types.Int)),

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

}