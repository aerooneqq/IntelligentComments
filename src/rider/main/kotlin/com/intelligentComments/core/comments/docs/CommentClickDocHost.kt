package com.intelligentComments.core.comments.docs

import com.intelligentComments.core.comments.popups.IntelligentCommentPopupManager
import com.intelligentComments.core.comments.resolver.FrontendReferenceResolverHost
import com.intelligentComments.core.domain.core.CommentIdentifier
import com.intelligentComments.core.domain.core.InvariantReference
import com.intelligentComments.core.domain.core.Reference
import com.intelligentComments.core.domain.rd.TextInvariantFromRdSegment
import com.intelligentComments.core.domain.rd.toRdReference
import com.intelligentComments.ui.comments.model.content.ContentSegmentsUiModel
import com.intellij.codeInsight.documentation.DocumentationManager
import com.intellij.openapi.components.service
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.event.EditorMouseEvent
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiDocumentManager
import com.intellij.ui.awt.RelativePoint
import com.intellij.ui.popup.PopupFactoryImpl
import com.intellij.util.application
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
  private val popupManager = project.service<IntelligentCommentPopupManager>()
  private val referenceResolver = project.service<FrontendReferenceResolverHost>()

  private var myCurrentReference: Reference? = null


  fun tryRequestHoverDoc(
    commentIdentifier: CommentIdentifier,
    reference: Reference,
    editor: Editor,
    contextPoint: Point,
  ) {
    application.assertIsDispatchThread()

    if (myCurrentReference == reference) {
      myCurrentReference = null
      return
    }

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

    val resolveRequest = RdReferenceResolveRequest(rdReference, textControlId)
    val request = RdCommentClickDocRequest(resolveRequest)
    commentsModel.requestClickDoc.start(serviceLifetime, request).result.advise(serviceLifetime) {
      val sessionId = it.unwrap() ?: return@advise
      val quickDocSession = quickDocModel.quickDocSessions[sessionId] ?: return@advise
      val psiFile = psiDocumentManager.getPsiFile(editor.document) ?: return@advise

      val contextElement = QuickDocElementWithInfo(quickDocSession.initialInfo, sessionId, psiFile, quickDocHost)

      editor.putUserData(PopupFactoryImpl.ANCHOR_POPUP_POINT, contextPoint.apply {
        y += additionalYDelta
      })

      myCurrentReference = reference
      documentationManager.showJavaDocInfo(editor, contextElement, null, true) {
        val sessions = quickDocModel.quickDocSessions
        if (sessions.containsKey(sessionId)) {
          sessions.remove(sessionId)
        }

        editor.putUserData(PopupFactoryImpl.ANCHOR_POPUP_POINT, null)
      }
    }
  }

  fun queueShowInvariantDoc(reference: InvariantReference, contextPoint: Point, e: EditorMouseEvent) {
    referenceResolver.resolveInvariantReference(reference, e.editor) {
      val invariantResolveResult = it as? RdInvariantResolveResult ?: return@resolveInvariantReference

      val invariant = TextInvariantFromRdSegment(invariantResolveResult.invariant, null, project)
      if (invariant.description.content.segments.isEmpty()) return@resolveInvariantReference

      val model = ContentSegmentsUiModel(project, null, invariant.description.content)
      val relativePoint = RelativePoint(e.mouseEvent.component, contextPoint)

      popupManager.showPopupFor(model, e.editor, relativePoint)
    }
  }
}