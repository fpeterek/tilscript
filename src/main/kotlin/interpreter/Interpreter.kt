package org.fpeterek.tilscript.interpreter.interpreter

import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import org.fpeterek.tilscript.interpreter.astprocessing.ASTConverter
import org.fpeterek.tilscript.interpreter.astprocessing.AntlrVisitor
import org.fpeterek.tilscript.interpreter.astprocessing.ErrorListener
import org.fpeterek.tilscript.interpreter.astprocessing.result.FunDefinition
import org.fpeterek.tilscript.interpreter.astprocessing.result.Sentences
import org.fpeterek.tilscript.interpreter.interpreter.builtins.*
import org.fpeterek.tilscript.interpreter.interpreter.builtins.Util
import org.fpeterek.tilscript.interpreter.interpreter.interpreterinterface.FnCallContext
import org.fpeterek.tilscript.interpreter.interpreter.interpreterinterface.FunctionInterface
import org.fpeterek.tilscript.interpreter.interpreter.interpreterinterface.InterpreterInterface
import org.fpeterek.tilscript.interpreter.reporting.Report
import org.fpeterek.tilscript.interpreter.reporting.ReportFormatter
import org.fpeterek.tilscript.interpreter.sentence.*
import org.fpeterek.tilscript.interpreter.types.*
import org.fpeterek.tilscript.interpreter.types.Util.isGeneric
import org.fpeterek.tilscript.interpreter.util.SrcPosition
import org.fpeterek.tilscript.interpreter.util.die
import org.fpeterek.tilscript.parser.TILScriptLexer
import org.fpeterek.tilscript.parser.TILScriptParser
import java.io.File
import java.nio.file.Paths


class Interpreter: InterpreterInterface {

    private val reportFormatter = ReportFormatter()
    private var baseDir = File(System.getProperty("user.dir"))

    private val symbolRepo = SymbolRepository()
    private val typeRepo = TypeRepository()

    private val topLevelFrame = StackFrame(parent = null)

    private val stack: MutableList<StackFrame> = mutableListOf(topLevelFrame)

    private val importedFiles = mutableSetOf<String>()

    private val currentFrame get() = stack.last()

    private val functions = mutableMapOf<String, TilFunction>()

    private val operatorFns = setOf("+", "-", "*", "/", "=", "<", ">")

    init {
        BuiltinsList.types.forEach(typeRepo::process)
        BuiltinsList.values.forEach(symbolRepo::define)

        BuiltinsList.functions.forEach { fn ->
            symbolRepo.define(fn.tilFunction)
            functions[fn.name] = fn.tilFunction
        }
    }

    private val numericOperators = mutableMapOf(
        "+" to NumericOperators.Plus,
        "-" to NumericOperators.Minus,
        "*" to NumericOperators.Multiply,
        "/" to NumericOperators.Divide,
        ">" to NumericOperators.Greater,
        "<" to NumericOperators.Less,
    )

    private fun defaultFrame() = StackFrame(parent = topLevelFrame)

    private fun pushFrame(frame: StackFrame) = stack.add(frame)

    private fun popFrame() = stack.removeLast()

    private fun <T> withFrame(frame: StackFrame, fn: () -> T): T {
        pushFrame(frame)
        val result = fn()
        popFrame()
        return result
    }

    private fun <T> withFrame(fn: () -> T): T = withFrame(defaultFrame(), fn)

    private infix fun Type.matches(other: Type) = TypeMatcher.match(this, other, typeRepo)

    override fun fnArgsMatch(fn: FunctionType, types: List<Type>): List<Boolean> =
        TypeMatcher.matchFnArgs(fn, types, typeRepo)

    override fun fnSignatureMatch(fn: FunctionType, returned: Type, args: List<Type>): Pair<Boolean, List<Boolean>> =
        TypeMatcher.matchFn(fn, returned, args, typeRepo)

    private fun interpret(variable: Variable): Construction {

        val frameVar = getVariableInternal(variable.name)

        if (frameVar.value == null) {
            die("Variable '${variable.name}' is declared but undefined")
        }

//        if (!(frameVar.constructedType matches variable.constructedType)) {
//            die("Mismatch between expected type (${variable.constructedType}) and actual type of variable (${frameVar.constructedType})")
//        }

        return frameVar.value
    }

    override fun getType(name: String) = typeRepo[name]

    override fun getFunction(name: String) = when (name) {
        in numericOperators -> numericOperators[name]!!.tilFunction
        "="                 -> EqualityOperator.tilFunction
        else                -> functions[name]
    }

    private fun getFunctionInt(fn: String): TilFunction =
        functions[fn] ?: die("Function '$fn' is not declared")

    fun getSymbol(symbol: String): Symbol = Symbol(
        symbol,
        SrcPosition(-1, -1, ""),
        symbolRepo[symbol] ?: die("Unknown symbol '${symbol}'"),
        listOf()
    )

    fun getSymbol(symbol: Symbol): Symbol = Symbol(
        symbol.value,
        symbol.position,
        symbolRepo[symbol.value] ?: die("Unknown symbol '${symbol.value}'"),
        symbol.reports
    )

    private fun getVariableInternal(name: String, frame: StackFrame?): Variable = when (frame) {
        null -> die("No such variable '$name'")
        else -> frame[name] ?: getVariableInternal(name, frame.parent)
    }

    private fun getVariableInternal(name: String): Variable = getVariableInternal(name, currentFrame)

    private fun getVariable(name: String, frame: StackFrame?): Variable? = when (frame) {
        null -> null
        else -> frame[name] ?: getVariable(name, frame.parent)
    }

    override fun getVariable(name: String): Variable? = getVariable(name, currentFrame)

    private fun interpret(triv: Trivialization) = when {
        triv.construction is TilFunction && triv.construction.name in numericOperators ->
            numericOperators[triv.construction.name]!!.tilFunction

        triv.construction is TilFunction && triv.construction.name == "=" -> EqualityOperator.tilFunction

        triv.construction is TilFunction -> getFunctionInt(triv.construction.name)

        // If the function was not known at parse-time, it could have been incorrectly detected as a Symbol instead
        triv.construction is Symbol && triv.construction.constructionType is Unknown &&
                triv.construction.value in functions -> getFunctionInt(triv.construction.value)

        triv.construction is Symbol && triv.construction.constructionType is Unknown ->
            getSymbol(triv.construction)

        triv.construction is Variable -> getVariableInternal(triv.construction.name)

        else -> triv.construction
    }

    private fun execute(construction: Construction, executions: Int): Construction = if (executions > 0) {
        execute(interpret(construction), executions-1)
    } else {
        construction
    }

    private fun interpret(execution: Execution) = execute(execution.construction, execution.executionOrder)

    private fun createLambdaCapture() = LambdaContext(currentFrame)

    private fun interpret(closure: Closure): TilFunction = withFrame {
        // We want to put variables introduced by the closure on the stack even if we aren't calling the
        // resulting function as of now to avoid capturing variables with the same name from a higher scope
        // This is necessary because we use the call stack to create captures
//        closure.variables.forEach(currentFrame::putVar)

        val lambda = LambdaFunction(closure.variables, closure.construction, createLambdaCapture(), returnType=closure.returnType)

        TilFunction(
            "<Lambda>",
            closure.position,
            lambda.signature,
            closure.reports,
            lambda,
        )
    }

    private fun interpretEquality(args: List<Construction>, ctx: FnCallContext) =
        EqualityOperator.apply(this, args, ctx)

    private fun interpretNumericOperator(fn: TilFunction, args: List<Construction>, ctx: FnCallContext): Construction =
        (numericOperators[fn.name] ?: die("No such operator '${fn.name}'"))
            .apply(this, args, ctx)

    private fun interpretOperator(fn: TilFunction, comp: Composition): Construction {

        if (comp.args.size != 2) {
            die("Operator '${fn.name}' expects 2 arguments (${comp.args.size} provided)")
        }

        val ctx = FnCallContext(comp.function.position)

        val intArgs = comp.args.map(::interpret)

        return when (fn.name) {
            "="  -> interpretEquality(intArgs, ctx)
            else -> interpretNumericOperator(fn, intArgs, ctx)
        }
    }

    private fun createCallsiteVar(ctx: FnCallContext) = Variable(
        "callsite",
        ctx.position,
        type=TupleType(Types.Text, Types.Int, Types.Int),
        value = TilTuple(
            listOf(
                Text(ctx.position.file, ctx.position),
                Integral(ctx.position.line.toLong(), ctx.position),
                Integral(ctx.position.char.toLong(), ctx.position),
            ),
            srcPos = ctx.position
        )
    )

    private fun interpretFnUsingFrame(fn: FunctionInterface, args: List<Construction>, ctx: FnCallContext, frame: StackFrame) =
        withFrame(frame) {
            fn.args.asSequence().zip(args.asSequence()).forEach { (variable, value) ->
                createLocal(variable, value)
            }
            fn(this, args, ctx)
        }

    private fun interpretNilAccepting(fn: FunctionInterface, args: List<Construction>, ctx: FnCallContext): Construction {

        val intArgs = withFrame(StackFrame(parent = currentFrame)) {
            createLocal(createCallsiteVar(ctx))
            args.map(::interpret)
        }

        return interpretFnUsingFrame(fn, intArgs, ctx, defaultFrame())
    }

    private fun interpretNilRefusing(fn: FunctionInterface, args: List<Construction>, ctx: FnCallContext): Construction {

        val intArgs = mutableListOf<Construction>()

        val frame = StackFrame(parent = currentFrame)
        withFrame(frame) {
            createLocal(createCallsiteVar(ctx))
        }

        for (arg in args) {

            val int = withFrame(frame) {
                interpret(arg)
            }

            if (int is Nil) {
                return int
            }

            intArgs.add(int)
        }

        return interpretFnUsingFrame(fn, intArgs, ctx, defaultFrame())
    }

    private fun interpretIf(args: List<Construction>, ctx: FnCallContext): Construction {

        if (args.size % 2 != 0) {
            die("If expects an even number of arguments")
        }

        var i = 0

        while (i < args.size) {

            val cond = interpret(args[i])

            if (cond is Nil) {
                return cond
            }

            if (!(cond.constructionType matches Types.Bool)) {
                die("Condition must be a Bool (received: ${cond.constructionType})")
            }

            if (cond !is Bool) {
                return Nil(ctx.position, reason="Condition must not be symbolic")
            }

            if (cond.value) {
                return interpret(args[i+1])
            }

            i += 2
        }

        return Nil(ctx.position, reason="No condition matched")
    }

    private fun interpretFn(fn: FunctionInterface, args: List<Construction>, ctx: FnCallContext) = when {
        fn is Util.If -> interpretIf(args, ctx)
        fn.acceptsNil -> interpretNilAccepting(fn, args, ctx)
        else          -> interpretNilRefusing(fn, args, ctx)
    }

    private fun interpretLambda(fn: LambdaFunction, args: List<Construction>, ctx: FnCallContext) =
        interpretFnUsingFrame(fn, args.map(::interpret), ctx, StackFrame(parent = fn.context.frame))

    private fun interpretFn(fn: TilFunction, comp: Composition): Construction {
        val fnImpl = when (fn.implementation) {
            null -> functions[fn.name]?.implementation
            else -> fn.implementation
        } ?: die("Function ${fn.name} is declared but undefined, application is impossible")

        val ctx = FnCallContext(comp.function.position)

        return when (fnImpl) {
            is LambdaFunction -> interpretLambda(fnImpl, comp.args, ctx)
            else              -> interpretFn(fnImpl, comp.args, ctx)
        }
    }

    private fun interpret(comp: Composition): Construction {
        val fn = interpret(comp.function)

        if (fn !is TilFunction) {
            die("Only functions can be applied on arguments. $fn is not a function")
        }

        return when (fn.name) {
            in operatorFns -> interpretOperator(fn, comp)
            else           -> interpretFn(fn, comp)
        }
    }

    override fun interpret(construction: Construction): Construction = when (construction) {
        is Closure        -> interpret(construction)
        is Composition    -> interpret(construction)
        is Execution      -> interpret(construction)
        is Trivialization -> interpret(construction)
        // Values cannot be executed as they by themselves do not construct anything
        // Nil also only ever constructs nil, but Nil is a Value
        is Value          -> nil
        // Functions too cannot be executed, functions can only be applied using compositions
        // Functions must be constructed using trivializations or closures
        is TilFunction    -> nil
        is Variable       -> interpret(construction)
    }

    override fun typesMatch(t1: Type, t2: Type) = t1 matches t2

    override fun ensureMatch(expected: Type, received: Type) {
        if (!(expected matches received)) {
            die("Type mismatch (expected: $expected, received: $received)")
        }
    }

    override fun createLocal(variable: Variable, value: Construction) {

        if (variable.name in currentFrame) {
            die("Redefinition of variable '${variable.name}'")
        }

        val varWithValue = Variable(
            variable.name,
            variable.position,
            variable.constructedType,
            variable.reports,
            value,
        )

        currentFrame.putVar(varWithValue)
    }

    private fun createLocal(variable: Variable) {

        if (variable.name in currentFrame) {
            die("Redefinition of variable '${variable.name}'")
        }

        currentFrame.putVar(variable)
    }

    private fun interpret(decl: FunctionDeclaration) = decl.functions.forEach {

        if (it.constructedType !is FunctionType) {
            die("Invalid function type")
        }

        if (it.name !in functions) {
            functions[it.name] = it
        } else {
            val fn = functions[it.name]!!

            if (!(fn.constructedType matches it.constructedType)) {
                die("Redeclaration of function '${fn.name}' with a different type")
            }
        }
    }

    private fun interpret(def: FunctionDefinition) {

        if (def.name in functions) {
            val declared = functions[def.name]!!
            if (declared.implementation != null) {
                die("Redefinition of function '${def.name}' with a conflicting implementation")
            }
            if (!(declared.constructedType matches def.signature)) {
                die("Redeclaration of function '${def.name}' with a different type")
            }
        }
        functions[def.name] = def.tilFunction
    }

    private fun interpret(lit: LiteralDeclaration) {
        lit.literals.forEach {

            if (it.constructedType.isGeneric) {
                invalidUseOfGenerics()
            }

            if (it.value in symbolRepo) {
                val declaredType = symbolRepo[it.value]!!
                if (!(declaredType matches lit.type)) {
                    die("Redeclaration of symbol '${it.value}' with a different type")
                }
            } else {
                symbolRepo.declare(it)
            }
        }
    }

    private fun interpret(typedef: TypeDefinition) {
        val alias = typedef.alias

        if (alias.type.isGeneric) {
            invalidUseOfGenerics()
        }

        if (alias.name in typeRepo) {
            val declaredType = typeRepo[alias.name]!!
            if (!(declaredType matches alias.type)) {
                die("Redeclaration of symbol '${alias.name}' with a different type")
            }
        } else {
            typeRepo.process(alias)
        }
    }

    private fun invalidUseOfGenerics(): Nothing =
        die("Generic types are only allowed in function definitions")

    private fun interpret(varDecl: VariableDeclaration) {
        varDecl.variables.forEach {

            if (it.constructedType.isGeneric) {
                invalidUseOfGenerics()
            }

            if (it.name in topLevelFrame) {
                val declared = topLevelFrame[it.name]!!
                if (!(declared.constructedType matches it.constructedType)) {
                    die("Redeclaration of variable '${it.name}' with a different type")
                }
            } else {
                topLevelFrame.putVar(it)
            }
        }
    }

    private fun interpret(varDef: VariableDefinition) {

        if (varDef.constructsType.isGeneric) {
            invalidUseOfGenerics()
        }

        if (varDef.name in topLevelFrame) {
            val declared = topLevelFrame[varDef.name]!!

            if (declared.value != null) {
                die("Redefinition of variable '${varDef.name}' with a new value")
            }

            if (!(declared.constructedType matches varDef.constructsType)) {
                die("Redeclaration of variable '${varDef.name}' with a different type")
            }
        }

        val value = interpret(varDef.construction)

        if (!(value.constructedType matches varDef.constructsType)) {
            die("Type of value assigned to variable '${varDef.name}' does not match expected type " +
                    "(expected: ${varDef.constructsType}, received: ${value.constructedType})")
        }

        topLevelFrame.putVar(varDef.variable.withValue(value))
    }

    private fun interpret(declaration: Declaration) {
        when (declaration) {
            is FunctionDeclaration -> interpret(declaration)
            is FunctionDefinition  -> interpret(declaration)
            is LiteralDeclaration  -> interpret(declaration)
            is TypeDefinition      -> interpret(declaration)
            is VariableDeclaration -> interpret(declaration)
            is VariableDefinition  -> interpret(declaration)
        }
    }

    private fun interpret(sentence: Sentence) {
        when (sentence) {
            is Construction    -> {
                val res = interpret(sentence)
                if (res is Nil) {
                    report(res)
                    die("Nil constructed by a top level construction. Aborting execution.")
                }
            }
            is Declaration     -> interpret(sentence)
            is ImportStatement -> interpretFile(sentence.file)
        }
    }

    private fun report(r: Report) = reportFormatter.terminalOutput(r)

    private fun report(nil: Nil) = report(Report(nil.reason, nil.position))

    private fun varDefToDecl(def: VariableDefinition) = VariableDeclaration(
        listOf(def.variable),
        def.position,
        def.reports,
    )

    private fun tryInterpret(sentence: Sentence) = try {
        interpret(sentence)
    } catch (e: Exception) {
        die(e)
    }

    private fun interpretTypeAliases(sentences: List<Sentence>) = sentences
        .asSequence()
        .filter {
            it is TypeDefinition
        }
        .forEach(::tryInterpret)

    private fun interpretDefinitions(sentences: List<Sentence>) = sentences
        .asSequence()
        .filter {
            it is FunctionDeclaration ||
            it is FunctionDefinition  ||
            it is VariableDefinition  ||
            it is VariableDeclaration ||
            it is LiteralDeclaration
        }
        .map {
            when (it) {
                is VariableDefinition -> varDefToDecl(it)
                else                  -> it
            }
        }
        .forEach(::tryInterpret)

    private fun interpretRest(sentences: List<Sentence>) = sentences
        .asSequence()
        .filterNot {
            it is FunctionDeclaration ||
            it is FunctionDefinition  ||
            it is VariableDeclaration ||
            it is TypeDefinition      ||
            it is LiteralDeclaration
        }
        .forEach(::tryInterpret)

    private fun interpret(sentences: List<Sentence>) {
        interpretTypeAliases(sentences)
        interpretDefinitions(sentences)
        interpretRest(sentences)
    }

    private fun printErrors(errors: Iterable<Report>, errorType: String) {
        println("-".repeat(80))
        println("$errorType errors")

        reportFormatter.terminalOutput(errors)

        println("-".repeat(80))
        println("\n")
    }

    private fun <T> withBaseDir(dir: File, fn: () -> T): T {

        val prev = baseDir
        baseDir = dir
        val retval = fn()
        baseDir = prev

        return retval
    }

    private fun interpretFileInt(file: String) {
        val stream = CharStreams.fromFileName(file)

        val errorListener = ErrorListener(file)

        val lexer = TILScriptLexer(stream)
        lexer.removeErrorListeners()
        lexer.addErrorListener(errorListener)

        val parser = TILScriptParser(CommonTokenStream(lexer))
        parser.removeErrorListeners()
        parser.addErrorListener(errorListener)

        val start = parser.start()

        val sentences = try {
            AntlrVisitor(file).visit(start)
        } catch (ignored: Exception) {
            Sentences(listOf(), SrcPosition(0, 0, file))
        }

        if (errorListener.hasErrors) {
            printErrors(errorListener.errors, "Syntax")
            die("Syntax error occurred")
        }
        if (parser.numberOfSyntaxErrors > 0) {
            println("Parsing failed (likely due to a syntax error which couldn't be properly detected)")
            die("Syntax error occurred")
        }

        val ctx = ASTConverter.convert(sentences)

        interpret(ctx.sentences)
    }

    private fun storeAndInterpretFile(file: File) {

        val absolute = file.absoluteFile.toString()

        if (absolute in importedFiles) {
            return
        }

        importedFiles.add(absolute)

        withBaseDir(file.absoluteFile.parentFile) {
            interpretFileInt(absolute)
        }
    }

    fun interpretFile(filename: String) {

        val file = File(filename)

        if (file.isAbsolute) {
            storeAndInterpretFile(file)
        }

        val relative = Paths.get(baseDir.toString(), file.toString())

        storeAndInterpretFile(relative.toFile())
    }

}
