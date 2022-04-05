package com.intelligentComments.core.comments

import com.intelligentComments.core.comments.docs.CommentClickDocHost
import com.intelligentComments.core.comments.navigation.CommentsNavigationHost
import com.intelligentComments.core.comments.popups.IntelligentCommentPopupManager
import com.intelligentComments.core.domain.core.*
import com.intelligentComments.ui.comments.model.content.ContentSegmentUiModel
import com.intelligentComments.ui.comments.model.content.tickets.TicketUiModel
import com.intellij.openapi.components.service
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.event.EditorMouseEvent
import com.intellij.openapi.project.Project
import com.intellij.ui.awt.RelativePoint
import com.intellij.util.application
import com.jetbrains.rd.platform.util.getLogger
import java.awt.Point

class HighlightersClickHandler(private val project: Project) {
  private val logger = getLogger<HighlightersClickHandler>()
  private val clickDocHost = project.service<CommentClickDocHost>()
  private val navigationHost = project.service<CommentsNavigationHost>()
  private val popupManager = project.service<IntelligentCommentPopupManager>()

  fun handleClick(
    highlighter: TextHighlighter,
    editor: Editor,
    contextPoint: Point,
    e: EditorMouseEvent
  ) {
    application.assertIsDispatchThread()

    val comment = tryFindComment(highlighter)
    if (comment == null) {
      logger.error("Failed to find comment for $highlighter")
      return
    }

    if (tryShowPopupForFrontedReference(e, editor, contextPoint, highlighter)) return

    val singleReference = highlighter.references.firstOrNull() ?: return
    if (singleReference is InvariantReference) {
      clickDocHost.queueShowInvariantDoc(singleReference, contextPoint, e)
      return
    }

    if (singleReference is BackendReference) {
      val reference = tryGetReferenceFrom(highlighter)
      clickDocHost.tryRequestHoverDoc(comment.identifier, reference ?: return, editor, contextPoint)
      return
    }
  }

  private fun tryShowPopupForFrontedReference(
    e: EditorMouseEvent,
    editor: Editor,
    contextPoint: Point,
    highlighter: TextHighlighter
  ): Boolean {
    val frontendReference = highlighter.references.filterIsInstance<FrontendPopupSourceReference>().firstOrNull()
    if (frontendReference != null) {
      if (frontendReference is FrontendTicketReference) {
        val point = RelativePoint(e.mouseEvent.component, contextPoint)
        val model = ContentSegmentUiModel.getFrom(project, null, frontendReference.model)
        popupManager.showPopupFor(model, editor, point)
        return true
      }
    }

    return false
  }

  private fun tryGetReferenceFrom(highlighter: TextHighlighter): Reference? {
    return highlighter.references.firstOrNull { it is BackendReference }
  }

  fun handleCtrlClick(highlighter: TextHighlighter, editor: Editor) {
    application.assertIsDispatchThread()

    val reference = tryGetReferenceFrom(highlighter)
    navigationHost.performNavigation(reference ?: return, editor)
  }
}