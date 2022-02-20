package com.intelligentComments.core.comments

import com.intelligentComments.core.domain.core.*
import com.intelligentComments.core.domain.impl.ContentProcessingStrategyImpl
import com.intelligentComments.core.domain.rd.DocCommentFromRd
import com.intelligentComments.core.domain.rd.GroupOfLineCommentsFromRd
import com.intelligentComments.core.domain.rd.InvalidCommentFromRd
import com.intelligentComments.core.domain.rd.MultilineCommentFromRd
import com.intellij.openapi.components.service
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.RangeMarker
import com.intellij.openapi.editor.markup.RangeHighlighter
import com.intellij.openapi.project.Project
import com.intellij.util.application
import com.jetbrains.rd.ide.model.*
import com.jetbrains.rd.platform.util.getLogger

class RiderCommentsCreator(private val project: Project) {
  companion object {
    private val logger = getLogger<RiderCommentsCreator>()
  }


  fun createComment(
    rdComment: RdComment,
    editor: Editor,
    commentRange: RangeMarker,
    highlighter: RangeHighlighter
  ): CommentBase {
    application.assertIsDispatchThread()
    return when(rdComment) {
      is RdDocComment -> createDocComment(rdComment, project, commentRange, highlighter)
      is RdGroupOfLineComments -> createGroupOfLinesComment(rdComment, project, commentRange, highlighter)
      is RdMultilineComment -> createMultilineComments(rdComment, project, commentRange, highlighter)
      is RdInvalidComment -> createInvalidComment(rdComment, project, commentRange, highlighter)
      else -> throw IllegalArgumentException(rdComment.javaClass.name)
    }
  }

  fun createDocComment(
    rdDocComment: RdDocComment,
    project: Project,
    commentRange: RangeMarker,
    highlighter: RangeHighlighter
  ): DocComment {
    application.assertIsDispatchThread()
    val docComment = DocCommentFromRd(rdDocComment, project, highlighter, commentRange)
    val segmentsPreprocessingStrategy = project.service<ContentProcessingStrategyImpl>()
    docComment.content.content.processSegments(segmentsPreprocessingStrategy)
    return docComment
  }

  fun createGroupOfLinesComment(
    rdGroupOfLineComments: RdGroupOfLineComments,
    project: Project,
    commentRange: RangeMarker,
    highlighter: RangeHighlighter
  ): GroupOfLineComments {
    application.assertIsDispatchThread()
    return GroupOfLineCommentsFromRd(rdGroupOfLineComments, project, highlighter, commentRange)
  }

  fun createMultilineComments(
    rdMultilineComment: RdMultilineComment,
    project: Project,
    commentRange: RangeMarker,
    highlighter: RangeHighlighter
  ): MultilineComment {
    application.assertIsDispatchThread()
    return MultilineCommentFromRd(rdMultilineComment, project, highlighter, commentRange)
  }

  fun createInvalidComment(
    rdInvalidComment: RdInvalidComment,
    project: Project,
    commentRange: RangeMarker,
    highlighter: RangeHighlighter
  ): InvalidComment {
    application.assertIsDispatchThread()
    return InvalidCommentFromRd(rdInvalidComment, project, highlighter, commentRange)
  }
}