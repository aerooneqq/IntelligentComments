package com.intelligentComments.core.comments

import com.intelligentComments.core.domain.core.CodeEntityReference
import com.intelligentComments.core.domain.core.CommentIdentifier
import com.intelligentComments.core.domain.core.Reference
import com.intelligentComments.core.domain.rd.toRdReference
import com.intellij.codeInsight.documentation.DocumentationManager
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiDocumentManager
import com.intellij.ui.popup.PopupFactoryImpl
import com.jetbrains.rd.ide.model.*
import com.jetbrains.rd.platform.util.getLogger
import com.jetbrains.rd.platform.util.idea.LifetimedService
import com.jetbrains.rdclient.document.textControlId
import com.jetbrains.rdclient.quickDoc.FrontendQuickDocHost
import com.jetbrains.rdclient.quickDoc.QuickDocElementWithInfo
import com.jetbrains.rider.projectView.solution
import java.awt.Point

class CommentClickDocHost(private val project: Project) : LifetimedService() {
  companion object {
    const val additionalYDelta = 5
  }

  private val logger = getLogger<CommentClickDocHost>()
  private val commentsModel = project.solution.rdCommentsModel
  private val quickDocModel = project.solution.quickDocHostModel
  private val documentationManager = DocumentationManager.getInstance(project)
  private val psiDocumentManager = PsiDocumentManager.getInstance(project)
  private val quickDocHost = FrontendQuickDocHost.getInstance(project)


  fun tryRequestHoverDoc(
    commentIdentifier: CommentIdentifier,
    reference: Reference,
    editor: Editor,
    contextPoint: Point,
  ) {
    val rdReference = reference.toRdReference(project)
    if (rdReference !is RdCodeEntityReference && rdReference !is RdProxyReference) {
      logger.error("Expected RdCodeEntityReference or RdProxyReference, got $rdReference for ${commentIdentifier.moniker}")
      return
    }

    val textControlId = editor.textControlId
    if (textControlId == null) {
      logger.error("Got null documentId for ${commentIdentifier.moniker}")
      return
    }

    val request = RdCommentClickDocRequest(rdReference, textControlId)
    commentsModel.requestClickDoc.start(serviceLifetime, request).result.advise(serviceLifetime) {
      val sessionId = it.unwrap() ?: return@advise
      val quickDocSession = quickDocModel.quickDocSessions[sessionId] ?: return@advise
      val psiFile = psiDocumentManager.getPsiFile(editor.document) ?: return@advise

      val contextElement = QuickDocElementWithInfo(quickDocSession.initialInfo, sessionId, psiFile, quickDocHost)

      editor.putUserData(PopupFactoryImpl.ANCHOR_POPUP_POINT, contextPoint.apply {
        y += additionalYDelta
      })

      documentationManager.showJavaDocInfo(editor, contextElement, null, true) {
        val sessions = quickDocModel.quickDocSessions
        if (sessions.containsKey(sessionId)) {
          sessions.remove(sessionId)
        }

        editor.putUserData(PopupFactoryImpl.ANCHOR_POPUP_POINT, null)
      }
    }
  }
}