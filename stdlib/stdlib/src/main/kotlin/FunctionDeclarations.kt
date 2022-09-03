package org.fpeterek.tilscript.stdlib

import org.fpeterek.tilscript.common.sentence.TilFunction
import org.fpeterek.tilscript.common.types.*
import org.fpeterek.tilscript.common.types.Util.intensionalize
import org.fpeterek.tilscript.common.SrcPosition

object FunctionDeclarations {

    private val nonrestrictedQuantifier
        get() = FunctionType(Types.Bool, FunctionType(Types.Bool, GenericType(1)))

    private val restrictedQuantifier
        get() = FunctionType(
            /* Returns a class of classes */ FunctionType(Types.Bool, FunctionType(Types.Bool, GenericType(1))),
            /* Accepts a class */ FunctionType(Types.Bool, GenericType(1))
        )

    private val binaryBoolean
        get() = FunctionType(Types.Bool, Types.Bool, Types.Bool)
    private val unaryBoolean
        get() = FunctionType(Types.Bool, Types.Bool)

    private val constructionTruthiness
        get() = FunctionType(Types.Bool, ConstructionType)
    private val propositionTruthiness
        get() = FunctionType(Types.Bool, Types.Bool.intensionalize())

    private val singularizer
        get() = FunctionType(
            /* The only item */ GenericType(1),
            /* Contained in this class */ FunctionType(Types.Bool, GenericType(1)))

    private val noPosition = SrcPosition(-1, -1)

    val builtinFunctions = listOf(

        // Won't receive implementation
        TilFunction("ForAll", noPosition, nonrestrictedQuantifier),
        TilFunction("Exist", noPosition, nonrestrictedQuantifier),
        TilFunction("Sing", noPosition, singularizer),

        TilFunction("Every", noPosition, restrictedQuantifier),
        TilFunction("Some", noPosition, restrictedQuantifier),
        TilFunction("No", noPosition, restrictedQuantifier),

        TilFunction("Sub", noPosition, FunctionType(ConstructionType, ConstructionType, ConstructionType, ConstructionType)),

        TilFunction("TrueC", noPosition, constructionTruthiness),
        TilFunction("FalseC", noPosition, constructionTruthiness),
        TilFunction("ImproperC", noPosition, constructionTruthiness),

        TilFunction("TrueP", noPosition, propositionTruthiness),
        TilFunction("FalseP", noPosition, propositionTruthiness),
        TilFunction("UndefP", noPosition, propositionTruthiness),
    )

}