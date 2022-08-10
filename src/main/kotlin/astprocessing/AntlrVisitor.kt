package org.fpeterek.til.typechecking.astprocessing

import org.antlr.v4.runtime.ParserRuleContext
import org.antlr.v4.runtime.Token
import org.antlr.v4.runtime.tree.ParseTree
import org.antlr.v4.runtime.tree.TerminalNode
import org.apache.commons.text.StringEscapeUtils
import org.fpeterek.til.parser.TILScriptBaseVisitor
import org.fpeterek.til.parser.TILScriptParser
import org.fpeterek.til.typechecking.astprocessing.result.*
import org.fpeterek.til.typechecking.util.SrcPosition

object AntlrVisitor : TILScriptBaseVisitor<IntermediateResult>() {

    private fun invalidState(): Nothing = throw RuntimeException("Invalid parser state")

    private fun Token.position() = SrcPosition(line, charPositionInLine)

    private fun ParserRuleContext.position() = start.position()

    private fun TerminalNode.position() = symbol.position()

    override fun visit(tree: ParseTree?) = when (tree) {
        null -> invalidState()
        !is TILScriptParser.StartContext -> invalidState()
        else -> visitStart(tree)
    }

    override fun visitStart(ctx: TILScriptParser.StartContext) =
        Sentences(ctx.sentence().map(::visitSentence), ctx.position())

    override fun visitSentence(ctx: TILScriptParser.SentenceContext) =
        visitSentenceContent(ctx.sentenceContent())

    override fun visitSentenceContent(ctx: TILScriptParser.SentenceContentContext) = when {
        ctx.construction()     != null -> visitConstruction(ctx.construction())
        ctx.funDefinition()    != null -> visitFunDefinition(ctx.funDefinition())
        ctx.globalVarDef()     != null -> visitGlobalVarDef(ctx.globalVarDef())
        ctx.globalVarDecl()    != null -> visitGlobalVarDecl(ctx.globalVarDecl())
        ctx.typeDefinition()   != null -> visitTypeDefinition(ctx.typeDefinition())
        ctx.entityDefinition() != null -> visitEntityDefinition(ctx.entityDefinition())

        else -> invalidState()
    }

    override fun visitTypeDefinition(ctx: TILScriptParser.TypeDefinitionContext) = TypeAlias(
        name=visitTypeName(ctx.typeName()),
        type=visitDataType(ctx.dataType()),
        srcPos= ctx.position(),
    )

    override fun visitEntityDefinition(ctx: TILScriptParser.EntityDefinitionContext) = EntityDef(
        names=ctx.entityName().map { visitEntityName(it) },
        type=visitDataType(ctx.dataType()),
        srcPos=ctx.position(),
    )

    override fun visitConstruction(ctx: TILScriptParser.ConstructionContext): Construction = when {
        ctx.variable()        != null -> visitVariable(ctx.variable())
        ctx.closure()         != null -> visitClosure(ctx.closure())
        ctx.nExecution()      != null -> visitNExecution(ctx.nExecution())
        ctx.composition()     != null -> visitComposition(ctx.composition())
        ctx.trivialization()  != null -> visitTrivialization(ctx.trivialization())
        ctx.listInitializer() != null -> visitListInitializer(ctx.listInitializer())

        else -> invalidState()
    }.let {
        when (ctx.WT()) {
            null -> it
            else -> it.extensionalize(ctx.WT().position())
        }
    }

    override fun visitListInitializer(ctx: TILScriptParser.ListInitializerContext) =
        ListInitializer(ctx.construction().map(::visitConstruction), ctx.position())

    override fun visitGlobalVarDecl(ctx: TILScriptParser.GlobalVarDeclContext) = GlobalVarDecl(
        ctx.variableName().map { visitVariableName(it) },
        visitDataType(ctx.dataType()),
        ctx.position()
    )

    override fun visitFunDefinition(ctx: TILScriptParser.FunDefinitionContext) = FunDefinition(
        visitEntityName(ctx.entityName()),
        visitTypedVariables(ctx.typedVariables()),
        visitDataType(ctx.dataType()),
        visitConstruction(ctx.construction()),
        ctx.position(),
    )

    override fun visitGlobalVarDef(ctx: TILScriptParser.GlobalVarDefContext) = GlobalVarDef(
        visitVariableName(ctx.variableName()),
        visitDataType(ctx.dataType()),
        visitConstruction(ctx.construction()),
        ctx.position(),
    )

    override fun visitDataType(ctx: TILScriptParser.DataTypeContext): DataType = when {
        ctx.builtinType()  != null -> visitBuiltinType(ctx.builtinType())
        ctx.listType()     != null -> visitListType(ctx.listType())
        ctx.tupleType()    != null -> visitTupleType(ctx.tupleType())
        ctx.userType()     != null -> visitUserType(ctx.userType())
        ctx.compoundType() != null -> visitCompoundType(ctx.compoundType())

        else -> invalidState()
    }.let {
        when (ctx.TW()) {
            null -> it
            else -> it.intensionalize(ctx.TW().position())
        }
    }

    override fun visitBuiltinType(ctx: TILScriptParser.BuiltinTypeContext) =
        DataType.PrimitiveType(TypeName(ctx.text, ctx.position()), ctx.position())

    override fun visitListType(ctx: TILScriptParser.ListTypeContext) =
        DataType.Collection.List(visitDataType(ctx.dataType()), ctx.position())

    override fun visitTupleType(ctx: TILScriptParser.TupleTypeContext) =
        DataType.Collection.Tuple(visitDataType(ctx.dataType()), ctx.position())

    override fun visitUserType(ctx: TILScriptParser.UserTypeContext) =
        DataType.PrimitiveType(visitTypeName(ctx.typeName()), ctx.position())

    override fun visitCompoundType(ctx: TILScriptParser.CompoundTypeContext) =
        DataType.ClassType(ctx.dataType().map { visitDataType(it) }, ctx.position())

    override fun visitVariable(ctx: TILScriptParser.VariableContext) =
        Construction.VarRef(visitVariableName(ctx.variableName()), ctx.position())

    override fun visitTrivialization(ctx: TILScriptParser.TrivializationContext) = Construction.Execution(
        order=0,
        construction = when {
            ctx.entity()       != null -> visitEntity(ctx.entity())
            ctx.construction() != null -> visitConstruction(ctx.construction())
            ctx.dataType()     != null -> visitDataType(ctx.dataType())
            else                       -> invalidState()
        },
        srcPos=ctx.position(),
    )

    override fun visitComposition(ctx: TILScriptParser.CompositionContext) = Construction.Composition(
        visitConstruction(ctx.construction(0)),
        ctx.construction().asSequence().drop(1).map { visitConstruction(it) }.toList(),
        ctx.position(),
    )

    override fun visitClosure(ctx: TILScriptParser.ClosureContext) = Construction.Closure(
        visitLambdaVariables(ctx.lambdaVariables()),
        visitConstruction(ctx.construction()),
        ctx.position(),
    )

    override fun visitLambdaVariables(ctx: TILScriptParser.LambdaVariablesContext) =
        visitOptTypedVariables(ctx.optTypedVariables())

    override fun visitNExecution(ctx: TILScriptParser.NExecutionContext) = Construction.Execution(
        order=ctx.EXEC().text.drop(1).takeWhile { it.isDigit() }.toInt(),
        construction=when {
            ctx.entity() != null -> visitEntity(ctx.entity())
            ctx.construction() != null -> visitConstruction(ctx.construction())
            else -> invalidState()
        },
        srcPos=ctx.position(),
    )

    override fun visitOptTypedVariables(ctx: TILScriptParser.OptTypedVariablesContext) =
        TypedVars(ctx.optTypedVariable().map(::visitOptTypedVariable), ctx.position())

    override fun visitOptTypedVariable(ctx: TILScriptParser.OptTypedVariableContext): TypedVar {

        val variable = visitVariableName(ctx.variableName()).name

        val type = when {
            ctx.dataType() == null -> null
            else -> visitDataType(ctx.dataType())
        }

        return TypedVar(variable, type, ctx.position())
    }

    override fun visitTypedVariables(ctx: TILScriptParser.TypedVariablesContext) =
        TypedVars(ctx.typedVariable().map(::visitTypedVariable), ctx.position())

    override fun visitTypedVariable(ctx: TILScriptParser.TypedVariableContext) = TypedVar(
        visitVariableName(ctx.variableName()).name,
        visitDataType(ctx.dataType()),
        ctx.position()
    )

    override fun visitEntity(ctx: TILScriptParser.EntityContext) = Entity.from(
        when {
            ctx.entityName() != null -> visitEntityName(ctx.entityName())
            ctx.number()     != null -> visitNumber(ctx.number())
            ctx.symbol()     != null -> visitSymbol(ctx.symbol())
            ctx.string()     != null -> visitString(ctx.string())

            else -> invalidState()
        }
    )

    private fun String.unescape() = StringEscapeUtils.unescapeJava(this)

    private fun formatStrLit(str: String) = str
        .drop(1)
        .dropLast(1)
        .unescape()

    override fun visitString(ctx: TILScriptParser.StringContext) = Entity.StringLit(
        value = formatStrLit(ctx.text),
        srcPos = ctx.position(),
    )

    override fun visitTypeName(ctx: TILScriptParser.TypeNameContext) =
        TypeName(visitUcname(ctx.ucname()))

    override fun visitEntityName(ctx: TILScriptParser.EntityNameContext) =
        EntityName(visitUcname(ctx.ucname()))

    override fun visitVariableName(ctx: TILScriptParser.VariableNameContext) =
        VarName(visitLcname(ctx.lcname()))

    override fun visitSymbol(ctx: TILScriptParser.SymbolContext) = Symbol(ctx.text.trim(), ctx.position())

    override fun visitNumber(ctx: TILScriptParser.NumberContext) = Numeric(ctx.text, ctx.position())

    override fun visitUcname(ctx: TILScriptParser.UcnameContext) = Symbol(ctx.text, ctx.position())

    override fun visitLcname(ctx: TILScriptParser.LcnameContext) = Symbol(ctx.text, ctx.position())
}
