package com.intelligentComments.core.comments

import com.intelligentComments.core.domain.core.*
import com.intelligentComments.core.domain.impl.ContentProcessingStrategyImpl
import com.intelligentComments.core.domain.rd.*
import com.intellij.openapi.components.service
import com.intellij.openapi.editor.RangeMarker
import com.intellij.openapi.editor.markup.RangeHighlighter
import com.intellij.openapi.project.Project
import com.intellij.util.application
import com.jetbrains.rd.ide.model.*

class RiderCommentsCreator(private val project: Project) {
  fun createComment(
    rdComment: RdComment,
    commentRange: RangeMarker,
    highlighter: RangeHighlighter
  ): CommentBase {
    application.assertIsDispatchThread()
    return when(rdComment) {
      is RdDocComment -> createDocComment(rdComment, project, commentRange, highlighter)
      is RdGroupOfLineComments -> createGroupOfLinesComment(rdComment, project, commentRange, highlighter)
      is RdMultilineComment -> createMultilineComments(rdComment, project, commentRange, highlighter)
      is RdInvalidComment -> createInvalidComment(rdComment, project, commentRange, highlighter)
      is RdDisableInspectionComment -> createDisablingInspectionsComment(rdComment, project, commentRange, highlighter)
      is RdInlineReferenceComment -> createInlineReferenceComment(rdComment, project, commentRange, highlighter)
      is RdInlineToDoComment -> createToDoComment(rdComment, project, commentRange, highlighter)
      is RdInlineHackComment -> createHackComment(rdComment, project, commentRange, highlighter)
      is RdInlineInvariantComment -> createInvariantComment(rdComment, project, commentRange, highlighter)
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

  fun createDisablingInspectionsComment(
    rdInvalidComment: RdDisableInspectionComment,
    project: Project,
    rangeMarker: RangeMarker,
    highlighter: RangeHighlighter
  ): DisablingInspectionsComment {
    application.assertIsDispatchThread()
    return DisablingCommentFromRd(rdInvalidComment, project, highlighter, rangeMarker)
  }

  fun createInlineReferenceComment(
    rdInlineReferenceComment: RdInlineReferenceComment,
    project: Project,
    rangeMarker: RangeMarker,
    highlighter: RangeHighlighter
  ): InlineReferenceCommentFromRd {
    application.assertIsDispatchThread()
    return InlineReferenceCommentFromRd(rdInlineReferenceComment, project, highlighter, rangeMarker)
  }

  fun createToDoComment(
    rdComment: RdInlineToDoComment,
    project: Project,
    rangeMarker: RangeMarker,
    highlighter: RangeHighlighter
  ): ToDoInlineComment {
    application.assertIsDispatchThread()
    return ToDoInlineCommentFromRd(rdComment, project, highlighter, rangeMarker)
  }

  fun createHackComment(
    rdComment: RdInlineHackComment,
    project: Project,
    rangeMarker: RangeMarker,
    highlighter: RangeHighlighter
  ): HackInlineComment {
    application.assertIsDispatchThread()
    return HackInlineCommentFromRd(rdComment, project, highlighter, rangeMarker)
  }

  fun createInvariantComment(
    rdComment: RdInlineInvariantComment,
    project: Project,
    rangeMarker: RangeMarker,
    highlighter: RangeHighlighter
  ): InvariantInlineComment {
    application.assertIsDispatchThread()
    return InvariantInlineCommentFromRd(rdComment, project, highlighter, rangeMarker)
  }
}