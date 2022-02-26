package com.intelligentComments.core.comments

import com.intelligentComments.core.comments.docs.CommentClickDocHost
import com.intelligentComments.core.comments.navigation.CommentsNavigationHost
import com.intelligentComments.core.comments.popups.IntelligentCommentPopupManager
import com.intelligentComments.core.domain.core.*
import com.intellij.openapi.components.service
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.event.EditorMouseEvent
import com.intellij.openapi.project.Project
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

    val singleReference = highlighter.references.firstOrNull() ?: return

    if (singleReference is InvariantReference) {
      clickDocHost.queueShowInvariantDoc(singleReference, contextPoint, e)
      return
    }

    if (singleReference is BackendReference) {
      val reference = tryGetCodeEntityOrProxyReferenceFrom(highlighter)
      clickDocHost.tryRequestHoverDoc(comment.identifier, reference ?: return, editor, contextPoint)
      return
    }
  }

  private fun tryGetCodeEntityOrProxyReferenceFrom(highlighter: TextHighlighter): Reference? {
    var reference = highlighter.references.firstOrNull { it is CodeEntityReference }
    if (reference == null) {
      reference = highlighter.references.firstOrNull { it is ProxyReference }
    }

    return reference
  }

  fun handleCtrlClick(highlighter: TextHighlighter, editor: Editor) {
    application.assertIsDispatchThread()

    val reference = tryGetCodeEntityOrProxyReferenceFrom(highlighter)
    navigationHost.performNavigation(reference ?: return, editor)
  }
}