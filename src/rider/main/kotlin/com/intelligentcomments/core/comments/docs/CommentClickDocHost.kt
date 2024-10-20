package com.intelligentcomments.core.comments.docs

import com.intelligentcomments.core.comments.popups.IntelligentCommentPopupManager
import com.intelligentcomments.core.comments.resolver.FrontendReferenceResolverHost
import com.intelligentcomments.core.domain.core.*
import com.intelligentcomments.core.domain.rd.ContentSegmentFromRd
import com.intelligentcomments.core.domain.rd.toIdeaHighlightedText
import com.intelligentcomments.core.domain.rd.toRdReference
import com.intelligentcomments.ui.comments.model.content.text.TextContentSegmentUiModel
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
import com.jetbrains.rider.quickDoc.FrontendQuickDocHost
import com.jetbrains.rider.quickDoc.QuickDocElementWithInfo
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

  private var reference: Reference? = null


  fun tryRequestHoverDoc(
    commentIdentifier: CommentIdentifier,
    reference: Reference,
    editor: Editor,
    contextPoint: Point,
  ) {
    application.assertIsDispatchThread()

    if (this.reference == reference) {
      this.reference = null
      return
    }

    val rdReference = reference.toRdReference(project)
    if (rdReference !is RdCodeEntityReference && rdReference !is RdProxyReference) {
      logger.warn("Expected RdCodeEntityReference or RdProxyReference, got $rdReference for ${commentIdentifier.moniker}")
      return
    }

    val textControlId = editor.textControlId
    if (textControlId == null) {
      logger.warn("Got null documentId for ${commentIdentifier.moniker}")
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

      this.reference = reference
      documentationManager.showJavaDocInfo(contextElement, null, true) {
        val sessions = quickDocModel.quickDocSessions
        if (sessions.containsKey(sessionId)) {
          sessions.remove(sessionId)
        }

        editor.putUserData(PopupFactoryImpl.ANCHOR_POPUP_POINT, null)
      }
    }
  }

  fun queueShowInvariantDoc(reference: NamedEntityReference, contextPoint: Point, e: EditorMouseEvent) {
    referenceResolver.resolveReference(reference, e.editor) {
      val relativePoint = RelativePoint(e.mouseEvent.component, contextPoint)

      if (it is RdInvalidResolveResult) {
        it.error ?: return@resolveReference

        val errorText = object : UniqueEntityImpl(), TextContentSegment {
          override val parent: Parentable? = null
          override val highlightedText: HighlightedText = it.error.toIdeaHighlightedText(project, this)
        }

        val model = TextContentSegmentUiModel(project, null, errorText)
        popupManager.showPopupFor(model, e.editor, relativePoint)
        return@resolveReference
      }

      val invariantResolveResult = it as? RdNamedEntityResolveResult ?: return@resolveReference
      val segment = invariantResolveResult.segment ?: return@resolveReference

      val segmentFromRd = ContentSegmentFromRd.getFrom(segment, null, project)
      val model = segmentFromRd.createUiModel(project, null)

      popupManager.showPopupFor(model, e.editor, relativePoint)
    }
  }
}