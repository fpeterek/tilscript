package org.fpeterek.til.typechecking.sentence

import org.fpeterek.til.typechecking.sentence.isexecutable.NonExecutable
import org.fpeterek.til.typechecking.types.*


class TilFunction(
    val name: String,
    type: Type = Unknown,
) : Construction(constructedType=type, constructionType=ConstructionType),
    NonExecutable {

    companion object {

        private val realOperation
            get() = FunctionType(AtomicType.Eta, AtomicType.Eta, AtomicType.Eta)

        private val restrictedQuantifier
            get() = FunctionType(FunctionType(AtomicType.Omicron, GenericType(1)), FunctionType(AtomicType.Omicron, GenericType(1)))

        private val binaryBoolean
            get() = FunctionType(AtomicType.Omicron, AtomicType.Omicron, AtomicType.Omicron)
        private val unaryBoolean
            get() = FunctionType(AtomicType.Omicron, AtomicType.Omicron)

        private val constructionTruthiness
            get() = FunctionType(AtomicType.Omicron, ConstructionType)
        private val propositionTruthiness
            get() = FunctionType(AtomicType.Omicron, CommonTypes.proposition)

        // TODO: Built-in functions
        // TODO: Built-in literals (true/false) - elsewhere, though, certainly not in this file

        val builtins = listOf(
            TilFunction("+", realOperation),
            TilFunction("-", realOperation),
            TilFunction("*", realOperation),
            TilFunction("/", realOperation),
            TilFunction("=", realOperation),

            TilFunction("ToInt", Unknown),

            TilFunction("ForAll", Unknown),
            TilFunction("Exist", Unknown),
            TilFunction("Sing", Unknown),

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

    override fun toString() = name

    val fullyTyped: Boolean
        get() = constructedType is FunctionType && constructedType.fullyTyped

    init {
        if (type !is Unknown && type !is FunctionType) {
            throw RuntimeException("Type of TilFunction must be Unknown or FunctionType")
        }
    }

}
