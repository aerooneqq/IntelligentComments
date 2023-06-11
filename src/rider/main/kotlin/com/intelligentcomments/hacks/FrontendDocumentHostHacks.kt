package com.intelligentcomments.hacks

import com.intellij.openapi.editor.Document
import com.intellij.openapi.project.Project
import com.jetbrains.rd.ide.model.RdDocumentId
import com.jetbrains.rd.util.reactive.ViewableMap
import com.jetbrains.rdclient.document.FrontendDocumentHost
import com.jetbrains.rdclient.editors.FrontendTextControlHost
import java.lang.reflect.Field


//maybe the second worst thing I've done in my life
class FrontendDocumentHostHacks(project: Project) {
    private val documentHost = FrontendDocumentHost.getInstance(project)
    private val field: Field = FrontendDocumentHost::class.java.getDeclaredField("openedDocuments")

    init {
        field.isAccessible = true
    }

    fun getOpenedDocuments(): ViewableMap<RdDocumentId, Document> {
        return field.get(documentHost) as ViewableMap<RdDocumentId, Document>
    }
}