package org.fpeterek.til.typechecking.astprocessing

import org.fpeterek.til.parser.TILScriptBaseVisitor
import org.fpeterek.til.parser.TILScriptParser
import org.fpeterek.til.typechecking.astprocessing.result.*

class ASTVisitor : TILScriptBaseVisitor<IntermediateResult>() {

    override fun visitStart(ctx: TILScriptParser.StartContext): IntermediateResult {
        return super.visitStart(ctx)
    }

    override fun visitSentence(ctx: TILScriptParser.SentenceContext): IntermediateResult {
        return super.visitSentence(ctx)
    }

    override fun visitSentenceContent(ctx: TILScriptParser.SentenceContentContext): IntermediateResult {
        return super.visitSentenceContent(ctx)
    }

    override fun visitTypeDefinition(ctx: TILScriptParser.TypeDefinitionContext): IntermediateResult {
        return super.visitTypeDefinition(ctx)
    }

    override fun visitEntityDefinition(ctx: TILScriptParser.EntityDefinitionContext): IntermediateResult {
        return super.visitEntityDefinition(ctx)
    }

    // TODO: Modify all classes which only hold references to Construction
    //       to store such references as Construction instances rather than
    //       IntermediateResult instances
    override fun visitConstruction(ctx: TILScriptParser.ConstructionContext): Construction = when {
        ctx.variable()       != null -> visitVariable(ctx.variable())
        ctx.closure()        != null -> visitClosure(ctx.closure())
        ctx.nExecution()     != null -> visitNExecution(ctx.nExecution())
        ctx.composition()    != null -> visitComposition(ctx.composition())
        ctx.trivialization() != null -> visitTrivialization(ctx.trivialization())

        else -> throw RuntimeException("Invalid parser state")
    }.let {
        when (ctx.WT()) {
            null -> it
            else -> it.extensionalize()
        }
    }

    override fun visitGlobalVarDef(ctx: TILScriptParser.GlobalVarDefContext) = GlobalVarDef(
        ctx.variableName().map { visitVariableName(it) },
        visitDataType(ctx.dataType())
    )

    // TODO: Modify all classes which only hold references to DataTypes
    //       to store such references as DataType instances rather than
    //       IntermediateResult instances
    override fun visitDataType(ctx: TILScriptParser.DataTypeContext): DataType = when {
        ctx.builtinType()  != null -> visitBuiltinType(ctx.builtinType())
        ctx.listType()     != null -> visitListType(ctx.listType())
        ctx.tupleType()    != null -> visitTupleType(ctx.tupleType())
        ctx.userType()     != null -> visitUserType(ctx.userType())
        ctx.compoundType() != null -> visitCompoundType(ctx.compoundType())

        else -> throw RuntimeException("Invalid parser state")
    }.let {
        when (ctx.TW()) {
            null -> it
            else -> it.intensionalize()
        }
    }

    override fun visitBuiltinType(ctx: TILScriptParser.BuiltinTypeContext) =
        DataType.PrimitiveType(TypeName(ctx.text))

    override fun visitListType(ctx: TILScriptParser.ListTypeContext) =
        DataType.Collection.List(visitDataType(ctx.dataType()))

    override fun visitTupleType(ctx: TILScriptParser.TupleTypeContext) =
        DataType.Collection.Tuple(visitDataType(ctx.dataType()))

    override fun visitUserType(ctx: TILScriptParser.UserTypeContext) =
        DataType.PrimitiveType(visitTypeName(ctx.typeName()))

    override fun visitCompoundType(ctx: TILScriptParser.CompoundTypeContext) =
        DataType.ClassType(ctx.dataType().map { visitDataType(it) })

    override fun visitVariable(ctx: TILScriptParser.VariableContext) =
        Construction.VarRef(visitVariableName(ctx.variableName()))

    override fun visitTrivialization(ctx: TILScriptParser.TrivializationContext) = Construction.Execution(
        order=0,
        construction = when {
            ctx.entity() != null -> visitEntity(ctx.entity())
            ctx.construction() != null -> visitConstruction(ctx.construction())
            else -> throw RuntimeException("Invalid parser state")
        }
    )

    override fun visitComposition(ctx: TILScriptParser.CompositionContext) = Construction.Composition(
        visitConstruction(ctx.construction(0)),
        ctx.construction().asSequence().drop(1).map { visitConstruction(it) }.toList()
    )

    override fun visitClosure(ctx: TILScriptParser.ClosureContext) = Construction.Closure(
        visitLambdaVariables(ctx.lambdaVariables()),
        visitConstruction(ctx.construction())
    )

    override fun visitLambdaVariables(ctx: TILScriptParser.LambdaVariablesContext) =
        visitTypedVariables(ctx.typedVariables())

    override fun visitNExecution(ctx: TILScriptParser.NExecutionContext) = Construction.Execution(
        order=ctx.EXEC().text.drop(1).takeWhile { it.isDigit() }.toInt(),
        construction=when {
            ctx.entity() != null -> visitEntity(ctx.entity())
            ctx.construction() != null -> visitConstruction(ctx.construction())
            else -> throw RuntimeException("Invalid parser state")
        }
    )

    override fun visitTypedVariables(ctx: TILScriptParser.TypedVariablesContext) =
        TypedVars(ctx.typedVariable().map(::visitTypedVariable))

    override fun visitTypedVariable(ctx: TILScriptParser.TypedVariableContext): TypedVar {

        val variable = visitVariableName(ctx.variableName())

        val type = when {
            ctx.typeName() == null -> TypeName("")
            else -> visitTypeName(ctx.typeName())
        }

        return TypedVar(variable, type)
    }

    override fun visitEntity(ctx: TILScriptParser.EntityContext) = Entity.from(
        when {
            ctx.entityName() != null -> visitEntityName(ctx.entityName())
            ctx.keyword() != null -> visitKeyword(ctx.keyword())
            ctx.number() != null -> visitNumber(ctx.number())
            ctx.symbol() != null -> visitSymbol(ctx.symbol())
            else -> throw RuntimeException("Parser error: All entity children are null.")
        }
    )

    override fun visitTypeName(ctx: TILScriptParser.TypeNameContext) =
        TypeName(visitUcname(ctx.ucname()))

    override fun visitEntityName(ctx: TILScriptParser.EntityNameContext) =
        EntityName(visitUcname(ctx.ucname()))

    override fun visitVariableName(ctx: TILScriptParser.VariableNameContext) =
        VarName(visitLcname(ctx.lcname()))

    override fun visitKeyword(ctx: TILScriptParser.KeywordContext) = Symbol(ctx.text)

    override fun visitAny(ctx: TILScriptParser.AnyContext) = Symbol(ctx.text)

    override fun visitSymbol(ctx: TILScriptParser.SymbolContext) = Symbol(ctx.text)

    override fun visitNumber(ctx: TILScriptParser.NumberContext) = Numeric(ctx.text)

    override fun visitUcname(ctx: TILScriptParser.UcnameContext) = Symbol(ctx.text)

    override fun visitLcname(ctx: TILScriptParser.LcnameContext) = Symbol(ctx.text)
}
