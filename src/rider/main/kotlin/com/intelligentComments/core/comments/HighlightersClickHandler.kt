package com.intelligentComments.core.comments

import com.intelligentComments.core.domain.core.CodeEntityReference
import com.intelligentComments.core.domain.core.ProxyReference
import com.intelligentComments.core.domain.core.TextHighlighter
import com.intelligentComments.core.domain.core.tryFindComment
import com.intellij.openapi.components.service
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.jetbrains.rd.platform.util.application
import com.jetbrains.rd.platform.util.getLogger
import java.awt.Point

class HighlightersClickHandler(project: Project) {
  private val logger = getLogger<HighlightersClickHandler>()
  private val clickDocHost = project.service<CommentClickDocHost>()


  fun handleClick(highlighter: TextHighlighter, editor: Editor, contextPoint: Point) {
    application.assertIsDispatchThread()

    val comment = tryFindComment(highlighter)
    if (comment == null) {
      logger.error("Failed to find comment for $highlighter")
      return
    }

    var reference = highlighter.references.firstOrNull { it is CodeEntityReference }
    if (reference == null) {
      reference = highlighter.references.firstOrNull { it is ProxyReference }
    }

    clickDocHost.tryRequestHoverDoc(comment.commentIdentifier, reference ?: return, editor, contextPoint)
  }
}