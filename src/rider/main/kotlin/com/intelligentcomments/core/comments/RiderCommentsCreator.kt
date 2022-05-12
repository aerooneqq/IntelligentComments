package com.intelligentcomments.core.comments

import com.intelligentcomments.core.domain.core.*
import com.intelligentcomments.core.domain.impl.ContentProcessingStrategyImpl
import com.intelligentcomments.core.domain.rd.*
import com.intellij.openapi.components.service
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.RangeMarker
import com.intellij.openapi.editor.markup.RangeHighlighter
import com.intellij.openapi.project.Project
import com.intellij.util.application
import com.jetbrains.rd.ide.model.*

class RiderCommentsCreator(private val project: Project) {
  fun createComment(
    editor: Editor,
    rdComment: RdComment,
    commentRange: RangeMarker,
    highlighter: RangeHighlighter
  ): CommentBase {
    application.assertIsDispatchThread()
    return when(rdComment) {
      is RdDocComment -> createDocComment(editor, rdComment, project, commentRange, highlighter)
      is RdGroupOfLineComments -> createGroupOfLinesComment(editor, rdComment, project, commentRange, highlighter)
      is RdMultilineComment -> createMultilineComments(editor, rdComment, project, commentRange, highlighter)
      is RdInvalidComment -> createInvalidComment(editor, rdComment, project, commentRange, highlighter)
      is RdDisableInspectionComment -> createDisablingInspectionsComment(editor, rdComment, project, commentRange, highlighter)
      is RdInlineReferenceComment -> createInlineReferenceComment(editor, rdComment, project, commentRange, highlighter)
      is RdInlineToDoComment -> createToDoComment(editor, rdComment, project, commentRange, highlighter)
      is RdInlineHackComment -> createHackComment(editor, rdComment, project, commentRange, highlighter)
      is RdInlineInvariantComment -> createInvariantComment(editor, rdComment, project, commentRange, highlighter)
      else -> throw IllegalArgumentException(rdComment.javaClass.name)
    }
  }

  fun createDocComment(
    editor: Editor,
    rdDocComment: RdDocComment,
    project: Project,
    commentRange: RangeMarker,
    highlighter: RangeHighlighter
  ): DocComment {
    application.assertIsDispatchThread()
    val docComment = DocCommentFromRd(rdDocComment, project, editor, highlighter, commentRange)
    val segmentsPreprocessingStrategy = project.service<ContentProcessingStrategyImpl>()
    docComment.content.content.processSegments(segmentsPreprocessingStrategy)
    return docComment
  }

  fun createGroupOfLinesComment(
    editor: Editor,
    rdGroupOfLineComments: RdGroupOfLineComments,
    project: Project,
    commentRange: RangeMarker,
    highlighter: RangeHighlighter
  ): GroupOfLineComments {
    application.assertIsDispatchThread()
    return GroupOfLineCommentsFromRd(rdGroupOfLineComments, project, editor, highlighter, commentRange)
  }

  fun createMultilineComments(
    editor: Editor,
    rdMultilineComment: RdMultilineComment,
    project: Project,
    commentRange: RangeMarker,
    highlighter: RangeHighlighter
  ): MultilineComment {
    application.assertIsDispatchThread()
    return MultilineCommentFromRd(rdMultilineComment, project, editor, highlighter, commentRange)
  }

  fun createInvalidComment(
    editor: Editor,
    rdInvalidComment: RdInvalidComment,
    project: Project,
    commentRange: RangeMarker,
    highlighter: RangeHighlighter
  ): InvalidComment {
    application.assertIsDispatchThread()
    return InvalidCommentFromRd(rdInvalidComment, project, editor, highlighter, commentRange)
  }

  fun createDisablingInspectionsComment(
    editor: Editor,
    rdInvalidComment: RdDisableInspectionComment,
    project: Project,
    rangeMarker: RangeMarker,
    highlighter: RangeHighlighter
  ): DisablingInspectionsComment {
    application.assertIsDispatchThread()
    return DisablingCommentFromRd(rdInvalidComment, project, editor, highlighter, rangeMarker)
  }

  fun createInlineReferenceComment(
    editor: Editor,
    rdInlineReferenceComment: RdInlineReferenceComment,
    project: Project,
    rangeMarker: RangeMarker,
    highlighter: RangeHighlighter
  ): InlineReferenceCommentFromRd {
    application.assertIsDispatchThread()
    return InlineReferenceCommentFromRd(rdInlineReferenceComment, project, editor, highlighter, rangeMarker)
  }

  fun createToDoComment(
    editor: Editor,
    rdComment: RdInlineToDoComment,
    project: Project,
    rangeMarker: RangeMarker,
    highlighter: RangeHighlighter
  ): ToDoInlineComment {
    application.assertIsDispatchThread()
    return ToDoInlineCommentFromRd(rdComment, project, editor, highlighter, rangeMarker)
  }

  fun createHackComment(
    editor: Editor,
    rdComment: RdInlineHackComment,
    project: Project,
    rangeMarker: RangeMarker,
    highlighter: RangeHighlighter
  ): HackInlineComment {
    application.assertIsDispatchThread()
    return HackInlineCommentFromRd(rdComment, project, editor, highlighter, rangeMarker)
  }

  fun createInvariantComment(
    editor: Editor,
    rdComment: RdInlineInvariantComment,
    project: Project,
    rangeMarker: RangeMarker,
    highlighter: RangeHighlighter
  ): InvariantInlineComment {
    application.assertIsDispatchThread()
    return InvariantInlineCommentFromRd(rdComment, project, editor, highlighter, rangeMarker)
  }
}