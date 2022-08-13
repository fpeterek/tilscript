package org.fpeterek.tilscript.interpreter.interpreter.builtins

import org.fpeterek.tilscript.interpreter.sentence.TilFunction
import org.fpeterek.tilscript.interpreter.types.*
import org.fpeterek.tilscript.interpreter.types.Util.intensionalize
import org.fpeterek.tilscript.interpreter.util.SrcPosition

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
        get() = FunctionType(Types.Bool, Types.Bool.intensionalize())

    private val singularizer
        get() = FunctionType(GenericType(1), FunctionType(Types.Bool, GenericType(1)))

    private val listCons
        get() = FunctionType(ListType(GenericType(1)), GenericType(1), ListType(GenericType(1)))

    private val noPosition = SrcPosition(-1, -1)

    val builtinFunctions = listOf(

        // TODO: Implement
        TilFunction("ToInt", noPosition, FunctionType(Types.Int, Types.Real)),
        TilFunction("ToReal", noPosition, FunctionType(Types.Real, Types.Int)),

        TilFunction("And", noPosition, binaryBoolean),
        TilFunction("Or", noPosition, binaryBoolean),
        TilFunction("Implies", noPosition, binaryBoolean),
        TilFunction("Not", noPosition, unaryBoolean),

        // Won't receive implementation
        TilFunction("ForAll", noPosition, nonrestrictedQuantifier),
        TilFunction("Exist", noPosition, nonrestrictedQuantifier),
        TilFunction("Sing", noPosition, singularizer),

        TilFunction("Every", noPosition, restrictedQuantifier),
        TilFunction("Some", noPosition, restrictedQuantifier),
        TilFunction("No", noPosition, restrictedQuantifier),

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