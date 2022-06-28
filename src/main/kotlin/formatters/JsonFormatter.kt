package org.fpeterek.til.typechecking.formatters

import org.fpeterek.til.typechecking.contextrecognition.Context
import org.fpeterek.til.typechecking.sentence.*
import org.fpeterek.til.typechecking.types.Type
import org.json.JSONArray
import org.json.JSONObject

object JsonFormatter {

    private fun sentenceBase(type: String, sentence: Sentence) = JSONObject()
        .put("sentenceType", type)
        .put("sentence", sentence.tsString())

    private fun constructionBase(sentenceType: String, constructedType: Type, context: Context, sentence: Sentence) =
        sentenceBase(sentenceType, sentence)
            .put("constructsType", constructedType.name)
            .put("context", context)

    fun format(fnDef: FunctionDeclaration): JSONObject = sentenceBase("Function declaration", fnDef)
        .put("functions", JSONArray().apply { fnDef.functions.forEach { put(it.name) } })
        .put("type", fnDef.type.name)

    fun format(litDef: LiteralDeclaration): JSONObject = sentenceBase("Literal definition", litDef)
        .put("literals", JSONArray().apply { litDef.literals.forEach { put(it.value) } })
        .put("type", litDef.type.name)

    fun format(typeDef: TypeDefinition): JSONObject = sentenceBase("Type definition", typeDef)
        .put("alias", typeDef.alias.name)
        .put("originalType", typeDef.alias.type.name)

    fun format(varDef: VariableDeclaration): JSONObject = sentenceBase("Variable declaration", varDef)
        .put("variables", JSONArray().apply { varDef.variables.forEach { put(it.name) } })
        .put("type", varDef.type.name)

    fun format(cl: Closure): JSONObject = constructionBase("Closure", cl.constructedType, cl.context, cl)
        .put(
            "variables",
            JSONArray().apply {
                cl.variables
                    .map { JSONObject().put("name", it.name).put("type", it.constructedType.name) }
                    .forEach { put(it) }
            }
        )
        .put("construction", format(cl.construction))

    fun format(comp: Composition): JSONObject =
        constructionBase("Composition", comp.constructedType, comp.context, comp)
            .put("function", format(comp.function))
            .put("arguments", JSONArray().apply { comp.args.forEach { put(format(it)) } })

    fun format(exec: Execution): JSONObject =
        constructionBase("Execution", exec.constructedType, exec.context, exec)
            .put("executionOrder", exec.executionOrder)
            .put("construction", exec.construction)

    fun format(lit: Literal): JSONObject =
        constructionBase("Literal", lit.constructedType, lit.context, lit)
            .put("value", lit.value)

    fun format(fn: TilFunction): JSONObject =
        constructionBase("Function", fn.constructedType, fn.context, fn)
            .put("function", fn.name)

    fun format(variable: Variable): JSONObject =
        constructionBase("Variable", variable.constructedType, variable.context, variable)
            .put("name", variable.name)

    private fun formatTrivializedLiteral(tr: Trivialization) =
        constructionBase("Trivialization", tr.constructedType, tr.context, tr)
            .put("literal", (tr.construction as Literal).value)

    private fun formatTrivializedVariable(tr: Trivialization) =
        constructionBase("Trivialization", tr.constructedType, tr.context, tr)
            .put("variable", (tr.construction as Variable).name)

    private fun formatTrivializedFn(tr: Trivialization) =
        constructionBase("Trivialization", tr.constructedType, tr.context, tr)
            .put("function", (tr.construction as TilFunction).name)

    private fun formatTrivialization(tr: Trivialization) =
        constructionBase("Trivialization", tr.constructedType, tr.context, tr)
            .put("construction", format(tr.construction))

    fun format(def: FunctionDefinition): JSONObject = sentenceBase("Function definition", def)
        .put("name", def.name)
        .put("signature", def.tilFunction.constructedType.name)
        .put("construction", format(def.construction))

    fun format(def: VariableDefinition): JSONObject = sentenceBase("Variable definition", def)
        .put("name", def.name)
        .put("type", def.constructsType.name)
        .put("initialization", format(def.construction))

    fun format(tr: Trivialization): JSONObject = when (tr.construction) {
        is Literal     -> formatTrivializedLiteral(tr)
        is TilFunction -> formatTrivializedFn(tr)
        is Variable    -> formatTrivializedVariable(tr)
        else           -> formatTrivialization(tr)
    }

    fun format(construction: Construction): JSONObject = when (construction) {
        is Closure        -> format(construction)
        is Composition    -> format(construction)
        is Execution      -> format(construction)
        is Literal        -> format(construction)
        is TilFunction    -> format(construction)
        is Trivialization -> format(construction)
        is Variable       -> format(construction)
    }

    fun format(declaration: Declaration): JSONObject = when (declaration) {
        is FunctionDeclaration -> format(declaration)
        is LiteralDeclaration  -> format(declaration)
        is TypeDefinition      -> format(declaration)
        is VariableDeclaration -> format(declaration)
        is FunctionDefinition  -> format(declaration)
        is VariableDefinition  -> format(declaration)
    }

    fun format(sentence: Sentence): JSONObject = when (sentence) {
        is Declaration   -> format(sentence)
        is Construction  -> format(sentence)
    }

    fun format(sentences: Iterable<Sentence>) = JSONArray().apply {
        sentences.forEach {
            put(format(it))
        }
    }

    fun asJson(sentences: Iterable<Sentence>) = format(sentences)

    fun asString(sentences: Iterable<Sentence>, pretty: Boolean = true) = when (pretty) {
        true -> format(sentences).toString(4)
        else -> format(sentences).toString()
    }

}
