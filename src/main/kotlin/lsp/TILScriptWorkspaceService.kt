package org.fpeterek.til.typechecking.lsp

import org.eclipse.lsp4j.DidChangeConfigurationParams
import org.eclipse.lsp4j.DidChangeWatchedFilesParams
import org.eclipse.lsp4j.services.WorkspaceService

class TILScriptWorkspaceService : WorkspaceService {
    override fun didChangeConfiguration(params: DidChangeConfigurationParams) {
        TODO("Not yet implemented")
    }

    override fun didChangeWatchedFiles(params: DidChangeWatchedFilesParams) {
        TODO("Not yet implemented")
    }
}
