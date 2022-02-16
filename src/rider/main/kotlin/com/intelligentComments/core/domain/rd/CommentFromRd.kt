package com.intelligentComments.core.domain.rd

import com.intelligentComments.core.comments.RiderCommentsCreator
import com.intelligentComments.core.domain.core.*
import com.intellij.openapi.components.service
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.RangeMarker
import com.intellij.openapi.editor.markup.RangeHighlighter
import com.intellij.openapi.project.Project
import com.jetbrains.rd.ide.model.*

abstract class CommentFromRd(
  project: Project,
  rangeMarker: RangeMarker,
  final override val correspondingHighlighter: RangeHighlighter
) : UniqueEntityImpl(), CommentBase {
  final override val parent: Parentable? = null
  final override val identifier: CommentIdentifier = CommentIdentifier.create(project, rangeMarker)

  abstract override fun recreate(editor: Editor): CommentBase
}

class DocCommentFromRd(
  private val rdDocComment: RdDocComment,
  private val project: Project,
  highlighter: RangeHighlighter,
  rangeMarker: RangeMarker,
) : CommentFromRd(project, rangeMarker, highlighter), DocComment {
  override val content: IntelligentCommentContent = IntelligentCommentContentFromRd(
    rdDocComment.content ?: RdIntelligentCommentContent(RdContentSegments(emptyList())),
    this,
    project
  )

  override fun isValid(): Boolean {
    return content.content.segments.isNotEmpty() && identifier.isValid
  }

  override fun recreate(editor: Editor): CommentBase {
    val creator = project.service<RiderCommentsCreator>()
    return creator.createDocComment(rdDocComment, project, identifier.rangeMarker, correspondingHighlighter)
  }
}

class GroupOfLineCommentsFromRd(
  private val rdComment: RdGroupOfLineComments,
  private val project: Project,
  highlighter: RangeHighlighter,
  rangeMarker: RangeMarker
) : CommentFromRd(project, rangeMarker, highlighter), GroupOfLineComments {
  override val text = ContentSegmentFromRd.getFrom(rdComment.text, this, project) as TextContentSegment

  override fun recreate(editor: Editor): CommentBase {
    val creator = project.service<RiderCommentsCreator>()
    return creator.createGroupOfLinesComment(rdComment, project, identifier.rangeMarker, correspondingHighlighter)
  }
}

class MultilineCommentFromRd(
  private val rdMultilineComment: RdMultilineComment,
  private val project: Project,
  highlighter: RangeHighlighter,
  rangeMarker: RangeMarker
) : CommentFromRd(project, rangeMarker, highlighter), MultilineComment {
  override val text: TextContentSegment = ContentSegmentFromRd.getFrom(rdMultilineComment.text, this, project) as TextContentSegment

  override fun recreate(editor: Editor): CommentBase {
    val creator = project.service<RiderCommentsCreator>()
    return creator.createMultilineComments(rdMultilineComment, project, identifier.rangeMarker, correspondingHighlighter)
  }
}