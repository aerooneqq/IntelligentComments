package com.intelligentcomments.core.comments

import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.Editor
import com.intellij.util.application
import com.jetbrains.rd.ide.model.RdDocumentId
import com.jetbrains.rd.ide.model.TextControlId
import com.jetbrains.rd.platform.util.getLogger
import com.jetbrains.rd.util.reactive.ViewableMap
import com.jetbrains.rdclient.editors.FrontendTextControlHost

class RiderOpenedEditorsAndDocuments {
    companion object {
        private val logger = getLogger<RiderOpenedEditorsAndDocuments>()
    }

    private val editorsToTextControlIds = mutableMapOf<Editor, TextControlId>()
    private val openedDocumentsCounts = mutableMapOf<RdDocumentId, Int>()

    val openedEditors = ViewableMap<TextControlId, Editor>()
    val openedDocuments = ViewableMap<RdDocumentId, Document>()


    fun handleEditorOpened(editor: Editor) {
        application.assertIsDispatchThread()

        val editorId = tryGetEditorId(editor) ?: return
        editorsToTextControlIds[editor] = editorId;

        if (!openedEditors.containsKey(editorId)) {
            openedEditors[editorId] = editor
        }

        if (!openedDocuments.containsKey(editorId.documentId)) {
            openedDocuments[editorId.documentId] = editor.document
            openedDocumentsCounts[editorId.documentId] = 1
        } else {
            val count = openedDocumentsCounts[editorId.documentId]!!
            openedDocumentsCounts[editorId.documentId] = count + 1
        }
    }

    private fun tryGetEditorId(editor: Editor): TextControlId? {
        val editorId = editor.getUserData(FrontendTextControlHost.textControlIdKey)
        if (editorId == null) {
            logger.warn("Failed to get textControlId for $editor")
        }

        return editorId
    }

    fun handleEditorClosed(editor: Editor) {
        application.assertIsDispatchThread()

        //can not obtain text control id directly from editor here, it is null for some reason :(
        val editorId = editorsToTextControlIds[editor] ?: return
        editorsToTextControlIds.remove(editor)

        assert(openedEditors.containsKey(editorId))
        openedEditors.remove(editorId)

        assert(openedDocuments.containsKey(editorId.documentId))
        assert(openedDocumentsCounts.containsKey(editorId.documentId))
        val documentsCount = openedDocumentsCounts[editorId.documentId]!!

        assert(documentsCount > 0)
        if (documentsCount == 1) {
            openedDocuments.remove(editorId.documentId)
            openedDocumentsCounts.remove(editorId.documentId)
            return
        }

        openedDocumentsCounts[editorId.documentId] = documentsCount - 1
    }
}