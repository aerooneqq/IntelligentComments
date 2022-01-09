package com.intelligentComments.core.domain.rd

import com.intelligentComments.core.comments.RiderCommentsCreator
import com.intelligentComments.core.domain.core.*
import com.intellij.openapi.components.service
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.RangeMarker
import com.intellij.openapi.project.Project
import com.jetbrains.rd.ide.model.*

abstract class CommentFromRd(
  private val rdComment: RdComment,
  private val project: Project,
  final override val rangeMarker: RangeMarker
) : UniqueEntityImpl(), CommentBase {
  final override val parent: Parentable? = null
  final override val commentIdentifier: CommentIdentifier = CommentIdentifier.create(rangeMarker.document, project, rangeMarker)

  abstract override fun recreate(editor: Editor): CommentBase
}

class DocCommentFromRd(
  private val rdDocComment: RdDocComment,
  private val project: Project,
  range: RangeMarker
) : CommentFromRd(rdDocComment, project, range), DocComment {
  override val content: IntelligentCommentContent = IntelligentCommentContentFromRd(
    rdDocComment.content ?: RdIntelligentCommentContent(RdContentSegments(emptyList())),
    this,
    project
  )

  override fun isValid(): Boolean {
    return content.content.segments.isNotEmpty() && rangeMarker.isValid
  }

  override fun recreate(editor: Editor): CommentBase {
    return project.service<RiderCommentsCreator>().createDocComment(rdDocComment, editor.project!!, rangeMarker)
  }
}

class GroupOfLineCommentsFromRd(
  private val rdComment: RdGroupOfLineComments,
  private val project: Project,
  range: RangeMarker
) : CommentFromRd(rdComment, project, range), GroupOfLineComments {
  override val text = ContentSegmentFromRd.getFrom(rdComment.text, this, project) as TextContentSegment

  override fun recreate(editor: Editor): CommentBase {
    return project.service<RiderCommentsCreator>().createGroupOfLinesComment(rdComment, project, rangeMarker)
  }
}

class MultilineCommentFromRd(
  private val rdMultilineComment: RdMultilineComment,
  private val project: Project,
  range: RangeMarker
) : CommentFromRd(rdMultilineComment, project, range), MultilineComment {
  override val text: TextContentSegment = ContentSegmentFromRd.getFrom(rdMultilineComment.text, this, project) as TextContentSegment

  override fun recreate(editor: Editor): CommentBase {
    return project.service<RiderCommentsCreator>().createMultilineComments(rdMultilineComment, project, rangeMarker)
  }
}