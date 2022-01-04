package com.intelligentComments.core.comments

import com.intelligentComments.core.domain.core.CodeEntityReference
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

    val reference = highlighter.references.firstOrNull { it is CodeEntityReference } ?: return

    clickDocHost.tryRequestHoverDoc(comment.commentIdentifier, reference as CodeEntityReference, editor, contextPoint)
  }
}