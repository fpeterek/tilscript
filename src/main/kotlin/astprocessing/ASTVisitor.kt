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

    override fun visitTerminator(ctx: TILScriptParser.TerminatorContext): IntermediateResult {
        return super.visitTerminator(ctx)
    }

    override fun visitTypeDefinition(ctx: TILScriptParser.TypeDefinitionContext): IntermediateResult {
        return super.visitTypeDefinition(ctx)
    }

    override fun visitEntityDefinition(ctx: TILScriptParser.EntityDefinitionContext): IntermediateResult {
        return super.visitEntityDefinition(ctx)
    }

    override fun visitConstruction(ctx: TILScriptParser.ConstructionContext): IntermediateResult {
        return super.visitConstruction(ctx)
    }

    override fun visitGlobalVarDef(ctx: TILScriptParser.GlobalVarDefContext): IntermediateResult {
        return super.visitGlobalVarDef(ctx)
    }

    override fun visitDataType(ctx: TILScriptParser.DataTypeContext): IntermediateResult {
        return super.visitDataType(ctx)
    }

    override fun visitBuiltinType(ctx: TILScriptParser.BuiltinTypeContext): IntermediateResult {
        return super.visitBuiltinType(ctx)
    }

    override fun visitListType(ctx: TILScriptParser.ListTypeContext): IntermediateResult {
        return super.visitListType(ctx)
    }

    override fun visitTupleType(ctx: TILScriptParser.TupleTypeContext): IntermediateResult {
        return super.visitTupleType(ctx)
    }

    override fun visitUserType(ctx: TILScriptParser.UserTypeContext): IntermediateResult {
        return super.visitUserType(ctx)
    }

    override fun visitCompoundType(ctx: TILScriptParser.CompoundTypeContext): IntermediateResult {
        return super.visitCompoundType(ctx)
    }

    override fun visitVariable(ctx: TILScriptParser.VariableContext): IntermediateResult {
        return super.visitVariable(ctx)
    }

    override fun visitTrivialization(ctx: TILScriptParser.TrivializationContext): IntermediateResult {
        return super.visitTrivialization(ctx)
    }

    override fun visitComposition(ctx: TILScriptParser.CompositionContext): IntermediateResult {
        return super.visitComposition(ctx)
    }

    override fun visitClosure(ctx: TILScriptParser.ClosureContext): IntermediateResult {
        return super.visitClosure(ctx)
    }

    override fun visitLambdaVariables(ctx: TILScriptParser.LambdaVariablesContext): IntermediateResult {
        return super.visitLambdaVariables(ctx)
    }

    override fun visitNExecution(ctx: TILScriptParser.NExecutionContext): IntermediateResult {
        // TODO: Continue
        ctx.EXEC()
        return super.visitNExecution(ctx)
    }

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
