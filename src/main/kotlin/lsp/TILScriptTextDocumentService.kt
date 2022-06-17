package org.fpeterek.til.typechecking.lsp

import org.eclipse.lsp4j.*
import org.eclipse.lsp4j.services.TextDocumentService
import java.util.concurrent.CompletableFuture

class TILScriptTextDocumentService : TextDocumentService {

    override fun rename(params: RenameParams): CompletableFuture<WorkspaceEdit> {
        return super.rename(params)
    }

    override fun didOpen(params: DidOpenTextDocumentParams) {
        TODO("Not yet implemented")
    }

    override fun didChange(params: DidChangeTextDocumentParams) {
        TODO("Not yet implemented")
    }

    override fun didClose(params: DidCloseTextDocumentParams) {
        TODO("Not yet implemented")
    }

    override fun didSave(params: DidSaveTextDocumentParams) {
        TODO("Not yet implemented")
    }
}
