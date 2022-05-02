package org.fpeterek.til.typechecking

import org.antlr.v4.runtime.CommonTokenStream
import org.antlr.v4.runtime.CharStreams
import org.fpeterek.til.parser.TILScriptLexer
import org.fpeterek.til.parser.TILScriptParser
import org.fpeterek.til.typechecking.astprocessing.ASTConverter
import org.fpeterek.til.typechecking.astprocessing.AntlrVisitor
import org.fpeterek.til.typechecking.astprocessing.ErrorListener
import org.fpeterek.til.typechecking.astprocessing.result.Sentences
import org.fpeterek.til.typechecking.contextrecognition.ContextRecognizer
import org.fpeterek.til.typechecking.formatters.JsonFormatter
import org.fpeterek.til.typechecking.types.Util.compose
import org.fpeterek.til.typechecking.types.Util.extensionalize
import org.fpeterek.til.typechecking.types.Util.trivialize
import org.fpeterek.til.typechecking.namechecker.NameChecker
import org.fpeterek.til.typechecking.reporting.Report
import org.fpeterek.til.typechecking.reporting.ReportFormatter
import org.fpeterek.til.typechecking.reporting.Reporter
import org.fpeterek.til.typechecking.sentence.*
import org.fpeterek.til.typechecking.tilscript.Builtins
import org.fpeterek.til.typechecking.typechecker.TypeChecker
import org.fpeterek.til.typechecking.types.SymbolRepository
import org.fpeterek.til.typechecking.tilscript.CommonTypes
import org.fpeterek.til.typechecking.types.TypeRepository
import org.fpeterek.til.typechecking.types.Util.intensionalize
import org.fpeterek.til.typechecking.util.SrcPosition
import java.io.File

fun printErrors(errors: Iterable<Report>, file: String, errorType: String) {
    println("-".repeat(80))
    println("File: $file")
    println("$errorType errors")

    ReportFormatter(file).terminalOutput(errors)

    println("-".repeat(80))
    println("\n")
}

private fun printErrorsForSentences(sentences: Iterable<Sentence>, file: String, errorType: String) =
    printErrors(Reporter.reportsAsList(sentences), file, errorType)

fun checkScript(filename: String) {
    val stream = CharStreams.fromFileName(filename)

    val errorListener = ErrorListener()

    val lexer = TILScriptLexer(stream)
    lexer.removeErrorListeners()
    lexer.addErrorListener(errorListener)
    val parser = TILScriptParser(CommonTokenStream(lexer))
    parser.removeErrorListeners()
    parser.addErrorListener(errorListener)

    val start = parser.start()

    val sentences = try {
        AntlrVisitor.visit(start)
    } catch (ignored: Exception) {
        Sentences(listOf(), SrcPosition(0, 0))
    }

    if (errorListener.hasErrors) {
        printErrors(errorListener.errors, filename, "Syntax")
        return
    }

    val script = ASTConverter.convert(sentences)

    val nameChecked = NameChecker.checkSymbols(script.sentences, SymbolRepository.withBuiltins())

    if (Reporter.containsReports(nameChecked)) {
        printErrorsForSentences(nameChecked, filename, "Name")
        return
    }

    val typeChecked = TypeChecker.process(
        nameChecked,
        symbolRepository = SymbolRepository.withBuiltins(),
        typeRepo = TypeRepository.withBuiltins()
    )

    if (Reporter.containsReports(typeChecked)) {
        printErrorsForSentences(typeChecked, filename, "Type")
    }

    val withContext = ContextRecognizer.assignContext(typeChecked)

    val json = JsonFormatter.asString(withContext)

    File("${File(filename).name}.json").writeText(json)

}

fun main(args: Array<String>) {

    args.forEach(::checkScript)

    // repeat(3) { println() }
    // test()

}

fun test() {

    val noPos = SrcPosition(-1, -1)

    val milda = Literal("Milda", noPos, Builtins.Iota)
    val varW = Variable("w", noPos, Builtins.Omega)
    val varT = Variable("t", noPos, Builtins.Tau)
    val alkoholik = Variable("alkoholik", noPos, Builtins.Iota)
    val presCr = TilFunction("President_CR", noPos, CommonTypes.office)
    val eq = TilFunction("=", noPos)

    val and = TilFunction("And", noPos)
    val or = TilFunction("Or", noPos)

    val zena = TilFunction("Zena", noPos, CommonTypes.property)
    val vdana = TilFunction("Vdana", noPos, CommonTypes.property)
    val maTitul = TilFunction("MaTitul", noPos, CommonTypes.property)

    val varX = Variable("x", noPos, type=Builtins.Iota)

    val jePani = Closure(
        listOf(varX),
        and.compose(
            zena.extensionalize(varW, varT).compose(varX),
            or.compose(
                vdana.extensionalize(varW, varT).compose(varX),
                maTitul.extensionalize(varW, varT).compose(varX)
            )
        ),
        noPos
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

    val whale = TilFunction("Whale", noPos, CommonTypes.property)
    val mammal = TilFunction("Mammal", noPos, CommonTypes.property)
    val all = TilFunction("All", noPos, CommonTypes.setOfSets)

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
