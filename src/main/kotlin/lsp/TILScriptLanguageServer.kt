package org.fpeterek.til.typechecking.lsp

import org.eclipse.lsp4j.*
import org.eclipse.lsp4j.jsonrpc.messages.Either
import org.eclipse.lsp4j.services.LanguageServer
import java.util.concurrent.CompletableFuture

class TILScriptLanguageServer : LanguageServer {

    private val workspaces = TILScriptWorkspaceService()
    private val textDocuments = TILScriptTextDocumentService()

    override fun initialize(params: InitializeParams?): CompletableFuture<InitializeResult> {

        if (params == null) {
            return CompletableFuture()
        }

        println("Received request from client ${params.clientInfo.name}-${params.clientInfo.version}")

        // No need for workspaces as TIL-Script only supports one file
        // val folders = params.workspaceFolders ?: listOf()

        val capabilities = ServerCapabilities()
        capabilities.textDocumentSync = Either.forLeft(TextDocumentSyncKind.Full)
        capabilities.completionProvider = CompletionOptions(false, listOf())

        val serverInfo = ServerInfo("TIL-Script Language Server", "0.1.0")

        return CompletableFuture.completedFuture(InitializeResult(capabilities, serverInfo))
    }

    override fun shutdown(): CompletableFuture<Any> = CompletableFuture.completedFuture(Unit)

    override fun exit() = Unit

    override fun getTextDocumentService() = textDocuments

    override fun getWorkspaceService() = workspaces

}