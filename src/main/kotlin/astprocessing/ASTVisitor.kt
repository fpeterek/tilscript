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

    override fun visitCompoundType(ctx: TILScriptParser.CompoundTypeContext) =
        ClassType(ctx.dataType().map { visitDataType(it) })

    override fun visitVariable(ctx: TILScriptParser.VariableContext) =
        VarRef(visitVariableName(ctx.variableName()))

    override fun visitTrivialization(ctx: TILScriptParser.TrivializationContext) = Execution(
        order=0,
        construction = when {
            ctx.entity() != null -> visitEntity(ctx.entity())
            ctx.construction() != null -> visitConstruction(ctx.construction())
            else -> throw RuntimeException("Invalid parser state")
        }
    )

    override fun visitComposition(ctx: TILScriptParser.CompositionContext) = Composition(
        visitConstruction(ctx.construction(0)),
        ctx.construction().asSequence().drop(1).map { visitConstruction(it) }.toList()
    )

    override fun visitClosure(ctx: TILScriptParser.ClosureContext) = Closure(
        visitLambdaVariables(ctx.lambdaVariables()),
        visitConstruction(ctx.construction())
    )

    override fun visitLambdaVariables(ctx: TILScriptParser.LambdaVariablesContext) =
        visitTypedVariables(ctx.typedVariables())

    override fun visitNExecution(ctx: TILScriptParser.NExecutionContext): Execution {
        val order = ctx.EXEC().text.drop(1).takeWhile { it.isDigit() }.toInt()

        val construction = when {
            ctx.entity() != null -> visitEntity(ctx.entity())
            ctx.construction() != null -> visitConstruction(ctx.construction())
            else -> throw RuntimeException("Invalid parser state")
        }

        return Execution(order, construction)
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
