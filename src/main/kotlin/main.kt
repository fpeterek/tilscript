package org.fpeterek.til.typechecking

import org.fpeterek.til.typechecking.util.Util.compose
import org.fpeterek.til.typechecking.util.Util.extensionalize
import org.fpeterek.til.typechecking.util.Util.trivialize
import org.fpeterek.til.typechecking.constructions.*
import org.fpeterek.til.typechecking.namechecker.NameChecker
import org.fpeterek.til.typechecking.util.SymbolRepository
import org.fpeterek.til.typechecking.types.AtomicType
import org.fpeterek.til.typechecking.types.FunctionType
import org.fpeterek.til.typechecking.util.CommonTypes
import org.fpeterek.til.typechecking.util.Util.intensionalize


fun main() {


    val milda = Variable("Milda", AtomicType.Iota)
    val varW = Variable("w", AtomicType.Omega)
    val varT = Variable("t", AtomicType.Tau)
    val presCr = TilFunction("President_CR", CommonTypes.office)
    val eq = TilFunction("=", FunctionType(AtomicType.Omicron, AtomicType.Iota, AtomicType.Iota))

    val and = TilFunction("and", CommonTypes.binaryOmicron)
    val or = TilFunction("or", CommonTypes.binaryOmicron)

    val zena = TilFunction("Zena", CommonTypes.office)
    val vdana = TilFunction("Vdana", CommonTypes.office)
    val maTitul = TilFunction("MaTitul", CommonTypes.office)

    val varX = Variable("x", type=AtomicType.Iota)

    val jePani = Closure(
        listOf(varX),
        and.compose(
            zena.extensionalize(varW, varT).compose(varX),
            or.compose(
                vdana.extensionalize(varW, varT).compose(varX),
                maTitul.extensionalize(varW, varT).compose(varX)
            )
        )
    ).intensionalize()

    val emanEqPresident = eq.trivialize().compose(
        presCr.extensionalize(varW, varT),
        milda.trivialize()
    )

    val whale = TilFunction("Whale", CommonTypes.property)
    val mammal = TilFunction("Mammal", CommonTypes.property)
    val all = TilFunction("All", CommonTypes.setOfSets)

    val mammalWhales = all.trivialize()
        .compose(whale.extensionalize(varW, varT))
        .compose(mammal.extensionalize(varW, varT))

    val symbolRepository = SymbolRepository()

    symbolRepository.add(presCr)
    symbolRepository.add(milda)
    symbolRepository.add(varW)
    symbolRepository.add(varT)
    symbolRepository.add(eq)
    symbolRepository.add(all)
    symbolRepository.add(whale)
    symbolRepository.add(mammal)
    symbolRepository.add(and)
    symbolRepository.add(or)
    symbolRepository.add(zena)
    symbolRepository.add(vdana)
    symbolRepository.add(maTitul)

    println(emanEqPresident)
    println(mammalWhales)
    println(CommonTypes.setOfSets)
    println(jePani)

    println(whale.isExecutable)
    println(mammalWhales.isExecutable)
    println(varW.isExecutable)

    NameChecker.checkSymbols(emanEqPresident, symbolRepository)
    NameChecker.checkSymbols(mammalWhales, symbolRepository)
    NameChecker.checkSymbols(jePani, symbolRepository)

}
