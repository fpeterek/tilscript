package org.fpeterek.til.typechecking

import org.antlr.v4.runtime.CommonTokenStream
import org.antlr.v4.runtime.CharStreams
import org.fpeterek.til.parser.TILScriptLexer
import org.fpeterek.til.parser.TILScriptParser
import org.fpeterek.til.typechecking.astprocessing.ASTConverter
import org.fpeterek.til.typechecking.astprocessing.AntlrVisitor
import org.fpeterek.til.typechecking.types.Util.compose
import org.fpeterek.til.typechecking.types.Util.extensionalize
import org.fpeterek.til.typechecking.types.Util.trivialize
import org.fpeterek.til.typechecking.namechecker.NameChecker
import org.fpeterek.til.typechecking.sentence.*
import org.fpeterek.til.typechecking.tilscript.Builtins
import org.fpeterek.til.typechecking.typechecker.TypeChecker
import org.fpeterek.til.typechecking.types.SymbolRepository
import org.fpeterek.til.typechecking.types.FunctionType
import org.fpeterek.til.typechecking.tilscript.CommonTypes
import org.fpeterek.til.typechecking.types.TypeRepository
import org.fpeterek.til.typechecking.types.Util.intensionalize


fun main() {
    val stream = CharStreams.fromFileName("skript.tils")

    val lexer = TILScriptLexer(stream)
    val parser = TILScriptParser(CommonTokenStream(lexer))

    val sentences = AntlrVisitor.visit(parser.start())

    val script = ASTConverter.convert(sentences)

    script.sentences.forEach(::println)

    TypeChecker.process(
        script.sentences,
        symbolRepository = SymbolRepository.withBuiltins(),
        typeRepo = TypeRepository.withBuiltins()
    )

    repeat(3) { println() }

    val milda = Literal("Milda", Builtins.Iota)
    val varW = Variable("w", Builtins.Omega)
    val varT = Variable("t", Builtins.Tau)
    val alkoholik = Variable("alkoholik", Builtins.Iota)
    val presCr = TilFunction("President_CR", CommonTypes.office)
    val eq = TilFunction("=")

    val and = TilFunction("And")
    val or = TilFunction("Or")

    val zena = TilFunction("Zena", CommonTypes.property)
    val vdana = TilFunction("Vdana", CommonTypes.property)
    val maTitul = TilFunction("MaTitul", CommonTypes.property)

    val varX = Variable("x", type=Builtins.Iota)

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

    // TODO: The following TIL construction may be improper due to a type mismatch,
    //       but the program reports an incorrect error
    //       The problem is caused by improper handling of construction type orders
    val alkoholikEqPresident = eq.trivialize().compose(
        presCr.extensionalize(varW, varT),
        alkoholik//.trivialize(),
    )

    val whale = TilFunction("Whale", CommonTypes.property)
    val mammal = TilFunction("Mammal", CommonTypes.property)
    val all = TilFunction("All", CommonTypes.setOfSets)

    val mammalWhales = all.trivialize()
        .compose(whale.extensionalize(varW, varT))
        .compose(mammal.extensionalize(varW, varT))

    val symbolRepository = SymbolRepository(
        presCr,
        milda,
        eq,
        all,
        whale,
        mammal,
        and,
        or,
        zena,
        vdana,
        maTitul,
        varW,
        varT,
        alkoholik,
        loadBuiltins = true
    )

    val lambdaBound = SymbolRepository(varW, varT, varX)

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
    NameChecker.checkSymbols(alkoholikEqPresident, symbolRepository)

    printRes(milda.trivialize(), symbolRepository, lambdaBound)
    printRes(emanEqPresident, symbolRepository, lambdaBound)
    printRes(alkoholikEqPresident, symbolRepository, lambdaBound)
    printRes(mammalWhales, symbolRepository, lambdaBound)
    printRes(jePani, symbolRepository, lambdaBound)

}

fun printRes(cons: Construction, repo: SymbolRepository, lambdaBound: SymbolRepository) {
    val analyzed = TypeChecker.process(cons, repo, lambdaBound)

    val asStr = analyzed.toString()

    println("-".repeat(asStr.length))
    println(asStr)
    println()
    println("Construction type: ${analyzed.constructionType}")
    println("Constructs: ${analyzed.constructedType}")
    println("-".repeat(asStr.length))
    println()
}
