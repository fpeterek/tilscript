package org.fpeterek.tilscript.interpreter.interpreter

import org.fpeterek.tilscript.common.SrcPosition
import org.fpeterek.tilscript.common.die
import org.fpeterek.tilscript.common.interpreterinterface.*
import org.fpeterek.tilscript.common.reporting.Report
import org.fpeterek.tilscript.common.reporting.ReportFormatter
import org.fpeterek.tilscript.common.sentence.*
import org.fpeterek.tilscript.common.sentence.EmptyList
import org.fpeterek.tilscript.common.types.*
import org.fpeterek.tilscript.common.types.Util.isGeneric
import org.fpeterek.tilscript.common.types.Util.trivialize
import org.fpeterek.tilscript.stdlib.*
import org.fpeterek.tilscript.stdlib.Util
import java.io.File
import java.nio.file.Paths


class Interpreter: InterpreterInterface {

    private val reportFormatter = ReportFormatter()
    private val interpretedFiles = mutableMapOf<String, ScriptContext>()

    private val defaultRunContext get() = RunContext(
        stack = mutableListOf(StackFrame(parent = null)),
        typeRepo = TypeRepository(),
        symbolRepo = SymbolRepository(),
        functions = mutableMapOf(),
        imports = mutableSetOf()
    )

    private var baseDir = File(System.getProperty("user.dir"))
    private var runContext = defaultRunContext

    private var scriptContext = ScriptContext("", listOf(), mutableMapOf())

    private val stack         get() = runContext.stack
    private val typeRepo      get() = runContext.typeRepo
    private val symbolRepo    get() = runContext.symbolRepo
    private val functions     get() = runContext.functions
    private val importedFiles get() = runContext.imports
    private val currentFrame  get() = runContext.currentFrame
    private val topLevelFrame get() = runContext.topLevelFrame

    private val operatorFns = setOf("+", "-", "*", "/", "=", "<", ">")

    private val numericOperators = mutableMapOf(
        "+" to NumericOperators.Plus,
        "-" to NumericOperators.Minus,
        "*" to NumericOperators.Multiply,
        "/" to NumericOperators.Divide,
        ">" to NumericOperators.Greater,
        "<" to NumericOperators.Less,
    )

    init {
        importStdlib()
    }

    private fun importStdlib() {
        StdlibRegistrar.types.forEach(typeRepo::process)
        StdlibRegistrar.values.forEach(symbolRepo::define)

        StdlibRegistrar.variables.forEach {
            if (it.value != null) {
                interpret(it.toDefinition())
            } else {
                interpret(it.toDeclaration())
            }
        }

        StdlibRegistrar.functions.forEach { fn ->
            symbolRepo.define(fn.tilFunction)
            functions[fn.name] = fn.tilFunction
        }
    }

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
            die("Variable '${variable.name}' is declared but undefined", variable.position)
        }

//        if (!(frameVar.constructedType matches variable.constructedType)) {
//            die("Mismatch between expected type (${variable.constructedType}) and actual type of variable (${frameVar.constructedType})")
//        }

        return frameVar.value as Construction
    }

    override fun getType(name: String) = typeRepo[name]

    override fun getFunction(name: String) = when (name) {
        in numericOperators -> numericOperators[name]!!.tilFunction
        "="                 -> EqualityOperator.tilFunction
        else                -> functions[name]
    }

    private fun getFunctionInt(fn: String, srcPosition: SrcPosition): TilFunction =
        functions[fn] ?: die("Function '$fn' is not declared", srcPosition)

    fun getSymbol(symbol: Symbol): Symbol = Symbol(
        symbol.value,
        symbol.position,
        symbolRepo[symbol.value] ?: die("Unknown symbol '${symbol.value}'", symbol.position),
        symbol.reports
    )

    private fun getVariableInternal(name: String, frame: StackFrameInterface?): Variable = when (frame) {
        null -> die("No such variable '$name'")
        else -> frame[name] ?: getVariableInternal(name, frame.parent)
    }

    private fun getVariableInternal(name: String): Variable = getVariableInternal(name, currentFrame)

    private fun getVariable(name: String, frame: StackFrameInterface?): Variable? = when (frame) {
        null -> null
        else -> frame[name] ?: getVariable(name, frame.parent)
    }

    override fun getVariable(name: String): Variable? = getVariable(name, currentFrame)

    private fun interpret(triv: Trivialization) = when {
        triv.construction is TilFunction && (triv.construction as TilFunction).name in numericOperators ->
            numericOperators[(triv.construction as TilFunction).name]!!.tilFunction

        triv.construction is TilFunction && (triv.construction as TilFunction).name == "=" -> EqualityOperator.tilFunction

        triv.construction is TilFunction -> getFunctionInt((triv.construction as TilFunction).name, triv.construction.position)

        // If the function was not known at parse-time, it could have been incorrectly detected as a Symbol instead
        triv.construction is Symbol && triv.construction.constructionType is Unknown &&
                (triv.construction as Symbol).value in functions -> getFunctionInt((triv.construction as Symbol).value, triv.construction.position)

        triv.construction is Symbol && triv.construction.constructionType is Unknown ->
            getSymbol(triv.construction as Symbol)

        triv.construction is Variable -> getVariableInternal((triv.construction as Variable).name)

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
        // Addendum: Not anymore, as we no longer create captures
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
        (numericOperators[fn.name] ?: die("No such operator '${fn.name}'", fn.position))
            .apply(this, args, ctx)

    private fun interpretOperator(fn: TilFunction, comp: Composition): Construction {

        if (comp.args.size != 2) {
            die("Operator '${fn.name}' expects 2 arguments (${comp.args.size} provided)", fn.position)
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

        val cond = interpret(args[0])

        if (cond is Nil) {
            return cond
        }

        if (!(cond.constructionType matches Types.Bool)) {
            die("Condition must be a Bool (received: ${cond.constructionType})", ctx.position)
        }

        if (cond !is Bool) {
            return Nil(ctx.position, reason="Condition must not be symbolic")
        }

        return when (cond.value) {
            true -> interpret(args[1])
            else -> interpret(args[2])
        }
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
        } ?: die("Function ${fn.name} is declared but undefined, application is impossible", fn.position)

        val ctx = FnCallContext(comp.function.position)

        return when (fnImpl) {
            is LambdaFunction -> interpretLambda(fnImpl, comp.args, ctx)
            else              -> interpretFn(fnImpl, comp.args, ctx)
        }
    }

    private fun interpret(comp: Composition): Construction {
        val fn = interpret(comp.function)

        if (fn !is TilFunction) {
            die("Only functions can be applied on arguments. $fn is not a function", fn.position)
        }

        return when (fn.name) {
            in operatorFns -> interpretOperator(fn, comp)
            else           -> interpretFn(fn, comp)
        }
    }

    private fun interpret(attrRef: AttributeReference): Construction {

        var value = getVariable(attrRef.attrs.first())
            ?: die("Variable ${attrRef.attrs.first()} does not exist", attrRef.position)


        attrRef.attrs.asSequence().drop(1).forEach {
            if (value.value == null) {
                die("Variable ${attrRef.attrs.first()} has no value", attrRef.position)
            }
            if (value.value is Symbol) {
                die("Cannot access attribute of a symbolic value", attrRef.position)
            }
            if (value.value !is Struct) {
                die("Non-struct values do not have attributes", attrRef.position)
            }

            if (!(value.value as Struct).has(it)) {
                die("Struct ${(value.value as Struct).structType} has no attribute $it")
            }

            value = (value.value as Struct)[it]!!
        }

        return value.value ?: Nil(attrRef.position, reason = "Struct attribute is Nil")
    }

    private fun constructList(cons: StructConstructor): Construction {

        if (cons.args.isNotEmpty()) {
            die("Constructor syntax can only be used to construct empty lists. Use 'ListOf to construct a non-empty list.")
        }

        val type = (cons.struct as ListType).type

        return EmptyList(valueType = type, srcPos = cons.position)
    }

    private fun interpret(cons: StructConstructor): Construction {

        if (cons.struct is ListType) {
            return constructList(cons)
        }

        val type = typeRepo[cons.struct.name]

        if (type == null || type !is StructType) {
            die("Cannot construct a non-struct type ${cons.struct.name}", cons.position)
        }

        if (cons.args.size != type.attributes.size) {
            die("Invalid number of arguments in constructor of struct ${cons.struct.name} " +
                    "(expected: ${type.attributes.size}, received: ${cons.args.size})", cons.position)
        }

        val interpreted = mutableListOf<Construction>()

        for (arg in cons.args) {
            val value = interpret(arg)

            if (value is Nil) {
                return value
            }

            interpreted.add(value)
        }

        interpreted.zip(type.attributes).forEach {
            val (con, exp) = it

            if (!typesMatch(con.constructedType, exp.constructedType)) {
                die("Type mismatch in constructor of struct ${cons.struct.name} " +
                        "expected: ${exp.constructedType}, received: ${con.constructedType})",
                    con.position)
            }
        }

        // I hate type erasure with utmost passion
        return Struct.fromConstructionList(interpreted, cons.position, type)
    }

    override fun interpret(construction: Construction): Construction = when (construction) {
        is Closure            -> interpret(construction)
        is Composition        -> interpret(construction)
        is Execution          -> interpret(construction)
        is Trivialization     -> interpret(construction)
        is AttributeReference -> interpret(construction)
        is Variable           -> interpret(construction)
        is StructConstructor  -> interpret(construction)

        // Values cannot be executed as they by themselves do not construct anything
        // Nil also only ever constructs nil, but Nil is a Value
        is Value       -> Nil(construction.position, reason = "Values cannot be executed")
        // Functions too cannot be executed, functions can only be applied using compositions
        // Functions must be constructed using trivializations or closures
        is TilFunction -> Nil(construction.position, reason = "Functions cannot be executed")
    }

    override fun typesMatch(t1: Type, t2: Type) = t1 matches t2

    override fun createLocal(variable: Variable, value: Construction) {

        if (variable.name in currentFrame) {
            die("Redefinition of variable '${variable.name}'", variable.position)
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
            die("Redefinition of variable '${variable.name}'", variable.position)
        }

        currentFrame.putVar(variable)
    }

    private fun interpret(decl: FunctionDeclaration) = decl.functions.forEach {

        if (it.constructedType !is FunctionType) {
            die("Invalid function type", it.position)
        }

        if (it.name !in functions) {
            functions[it.name] = it
        } else {
            val fn = functions[it.name]!!

            if (!(fn.constructedType matches it.constructedType)) {
                die("Redeclaration of function '${fn.name}' with a different type", it.position)
            }
        }
    }

    private fun defineFn(name: String, fn: TilFunction) {

        if (name in functions) {
            val declared = functions[name]!!
            if (declared.implementation != null) {
                die("Redefinition of function '${name}' with a conflicting implementation", fn.position)
            }
            if (!(declared.constructionType matches fn.constructionType)) {
                die("Redeclaration of function '${name}' with a different type", fn.position)
            }
        }
        functions[name] = fn
    }

    private fun interpret(def: FunctionDefinition) = defineFn(def.name, def.tilFunction)


    private fun interpret(lit: LiteralDeclaration) {
        lit.literals.forEach {

            if (it.constructedType.isGeneric) {
                invalidUseOfGenerics(it.position)
            }

            if (it.value in symbolRepo) {
                val declaredType = symbolRepo[it.value]!!
                if (!(declaredType matches lit.type)) {
                    die("Redeclaration of symbol '${it.value}' with a different type", it.position)
                }
            } else {
                symbolRepo.declare(it)
            }
        }
    }

    private fun interpret(typedef: TypeDefinition) {
        val alias = typedef.alias

        if (alias.type.isGeneric) {
            invalidUseOfGenerics(typedef.position)
        }

        if (alias.name in typeRepo) {
            val declaredType = typeRepo[alias.name]!!
            if (!(declaredType matches alias.type)) {
                die("Redeclaration of symbol '${alias.name}' with a different type", typedef.position)
            }
        } else {
            typeRepo.process(alias)
        }
    }

    private fun invalidUseOfGenerics(pos: SrcPosition): Nothing =
        die("Generic types are only allowed in function definitions", pos)

    private fun interpret(varDecl: VariableDeclaration) {
        varDecl.variables.forEach {

            if (it.constructedType.isGeneric) {
                invalidUseOfGenerics(it.position)
            }

            if (it.name in topLevelFrame) {
                val declared = topLevelFrame[it.name]!!
                if (!(declared.constructedType matches it.constructedType)) {
                    die("Redeclaration of variable '${it.name}' with a different type", it.position)
                }
            } else {
                topLevelFrame.putVar(it)
            }
        }
    }

    private fun interpret(varDef: VariableDefinition) {

        if (varDef.constructsType.isGeneric) {
            invalidUseOfGenerics(varDef.position)
        }

        if (varDef.name in topLevelFrame) {
            val declared = topLevelFrame[varDef.name]!!

            if (declared.value != null) {
                die("Redefinition of variable '${varDef.name}' with a var value", varDef.position)
            }

            if (!(declared.constructedType matches varDef.constructsType)) {
                die("Redeclaration of variable '${varDef.name}' with a different type", varDef.position)
            }
        }

        val value = interpret(varDef.construction)

        if (!(value.constructedType matches varDef.constructsType)) {
            die("Type of value assigned to variable '${varDef.name}' does not match expected type " +
                    "(expected: ${varDef.constructsType}, received: ${value.constructedType})", varDef.position)
        }

        if (value is Nil) {
            die("Nil constructed when initializing variable", value.position)
        }

        scriptContext.putVar(varDef.name, value)
        topLevelFrame.putVar(varDef.variable.withValue(value))
    }

    private fun declareStruct(structDefinition: StructDefinition) {
        if (structDefinition.struct.name !in typeRepo) {
            typeRepo.process(StructType(structDefinition.struct.name))
        }
    }

    private fun interpret(structDefinition: StructDefinition) {
        val unprocessed = structDefinition.struct

        // Catch conflicting redefinition early
        // If the struct has been declared, but not defined, it will not be frozen yet,
        // we want to modify the struct inside the repository
        // If the struct has been frozen, it means the struct is already well-defined, and we just
        // need to detect conflicting definitions
        // The freezing mechanism allows for implementation of structs with cyclic references
        val struct = when (unprocessed.name) {
             in typeRepo -> {
                 val fromRepo = typeRepo[unprocessed.name]!!

                 when {
                     fromRepo!is StructType -> die("Conflicting redefinition of type ${unprocessed.name}", structDefinition.position)
                     !fromRepo.frozen -> fromRepo
                     else -> StructType(unprocessed.name)
                 }
             }
            else -> StructType(unprocessed.name)
        }

        unprocessed.attributes.forEach {
            if ((it.constructedType is AtomicType || it.constructedType is StructType ||
                    it.constructedType is TypeAlias || it.constructedType is TypeName)
                    && it.constructedType.name !in typeRepo) {
                die("Unknown type ${it.constructedType}", it.position)
            }
            if (it.constructedType.isGeneric) {
                die("Struct attributes cannot be of a generic type", it.position)
            }

            val type = when (it.constructedType) {
                is TypeName -> typeRepo[it.constructedType.name]!!
                else -> it.constructedType
            }
            val attr = Variable(it.name, type = type, srcPos = it.position)

            struct.addAttribute(attr)
        }

        if (struct.name in typeRepo) {
            if (!(struct matches typeRepo[struct.name]!!)) {
                die("Conflicting redefinition of struct ${struct.name}", structDefinition.position)
            }
        }

        struct.freeze()
    }

    private fun interpret(declaration: Declaration) {
        when (declaration) {
            is FunctionDeclaration -> interpret(declaration)
            is FunctionDefinition  -> interpret(declaration)
            is LiteralDeclaration  -> interpret(declaration)
            is TypeDefinition      -> interpret(declaration)
            is VariableDeclaration -> interpret(declaration)
            is VariableDefinition  -> interpret(declaration)
            is StructDefinition    -> interpret(declaration)
        }
    }

    private fun interpret(import: ImportStatement) = when (import.file.startsWith("class://")) {
        true -> loadFromJar(import.file.removePrefix("class://"))
        else -> interpretImportedFile(import.file)
    }

    private fun interpret(sentence: Sentence) {
        when (sentence) {
            is Construction    -> {
                val res = interpret(sentence)
                if (res is Nil) {
                    report(res)
                    die("Nil constructed by a top level construction. Aborting execution.", res.position)
                }
            }
            is Declaration     -> interpret(sentence)
            is ImportStatement -> interpret(sentence)
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
            it is LiteralDeclaration  ||
            it is StructDefinition
        }
        .forEach(::tryInterpret)

    private fun declareStructs(sentences: List<Sentence>) = sentences
        .asSequence()
        .filterIsInstance<StructDefinition>()
        .forEach(::declareStruct)

    private fun defineStructs(sentences: List<Sentence>) = sentences
        .asSequence()
        .filterIsInstance<StructDefinition>()
        .forEach(::interpret)

    private fun interpret(sentences: List<Sentence>) {
        declareStructs(sentences)
        interpretTypeAliases(sentences)
        defineStructs(sentences)
        interpretDefinitions(sentences)
        interpretRest(sentences)
    }

    private fun loadFromJar(registrar: String) {

        if (registrar in importedFiles) {
            return
        }
        importedFiles.add(registrar)

        val reg = Class.forName(registrar).constructors.first().newInstance() as SymbolRegistrar

        val structs = reg.structs.map { it.toDefinition() }

        structs.forEach(::declareStruct)
        structs.forEach(::interpret)

        reg.functions.forEach {
            if (it !is DefaultFunction) {
                die("Only instances of DefaultFunction can be registered")
            }
            defineFn(it.name, it.tilFunction)
        }

        reg.variables.forEach {
            if (it.value != null) {
                interpret(it.toDefinition())
            } else {
                interpret(it.toDeclaration())
            }
        }

        reg.aliases.forEach { interpret(it.toDefinition()) }

        reg.symbols.forEach { interpret(it.toDeclaration()) }

        reg.functionDeclarations.forEach { interpret(it.toDeclaration()) }
    }

    private fun <T> withBaseDir(dir: File, fn: () -> T): T {

        val prev = baseDir
        baseDir = dir
        val retval = fn()
        baseDir = prev

        return retval
    }

    private fun interpretFileInt(file: String) =
        setupContextAndInterpret(Parser.parse(file))

    private fun setupContextAndInterpret(ctx: ScriptContext): ScriptContext {
        val oldCtx = scriptContext
        scriptContext = ctx

        interpret(ctx.sentences)

        scriptContext = oldCtx
        return ctx
    }

    private fun interpretAndStoreFile(file: File) {

        val absolute = file.absolutePath

        importedFiles.add(absolute)

        if (absolute in interpretedFiles) {
            return
        }

        withBaseDir(file.absoluteFile.parentFile) {
            interpretedFiles[file.absolutePath] = interpretFileInt(absolute)
        }
    }

    private fun absoluteFile(filename: String): File {
        val file = File(filename)

        return when (file.isAbsolute) {
            true -> file
            else -> Paths.get(baseDir.toString(), file.toString()).toFile()
        }
    }

    private fun importSymbolsFromInterpretedFile(file: String) {
        val abs = absoluteFile(file).absolutePath
        val ctx = interpretedFiles[abs] ?: die("Interpreter error: file '$abs' has not been interpreted")

        ctx.declarations.forEach {
            if (it is VariableDefinition) {
                val value = ctx.getVar(it.name) ?: die("Interpreter error: definition of variable ${it.name} has not been evaluated")

                val def = VariableDefinition(it.name, value.constructedType, value.trivialize(), it.position, listOf())

                interpret(def)
            } else {
                interpret(it)
            }
        }
    }

    private fun interpretImportedFile(filename: String) {
        if (filename in importedFiles) {
            return
        }

        val oldCtx = runContext
        runContext = defaultRunContext
        importStdlib()
        interpretAndStoreFile(absoluteFile(filename))
        runContext = oldCtx
        importSymbolsFromInterpretedFile(filename)
    }

    fun interpretFile(filename: String) {
        interpretAndStoreFile(absoluteFile(filename))
    }

}
