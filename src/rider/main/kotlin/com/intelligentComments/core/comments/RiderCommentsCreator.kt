package com.intelligentComments.core.comments

import com.intelligentComments.core.domain.core.DocComment
import com.intelligentComments.core.domain.impl.ContentProcessingStrategyImpl
import com.intelligentComments.core.domain.rd.DocCommentFromRd
import com.intellij.openapi.components.service
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.RangeMarker
import com.intellij.openapi.editor.impl.EditorImpl
import com.jetbrains.rd.ide.model.RdDocComment
import com.jetbrains.rd.platform.diagnostics.logAssertion
import com.jetbrains.rd.platform.util.getLogger

class RiderCommentsCreator {
  companion object {
    private val logger = getLogger<RiderCommentsCreator>()
  }

  fun tryCreateDocComment(
    rdDocComment: RdDocComment,
    editor: Editor,
    commentRange: RangeMarker
  ) : DocComment? {
    val project = editor.project
    if (project == null) {
      logger.logAssertion("Project was null for $editor")
      return null
    }

    val docComment = DocCommentFromRd(rdDocComment, project, commentRange)
    val segmentsPreprocessingStrategy = project.service<ContentProcessingStrategyImpl>()
    docComment.content.processSegments(segmentsPreprocessingStrategy)
    return docComment
  }
}