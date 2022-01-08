package com.intelligentComments.core.domain.rd

import com.intelligentComments.core.comments.RiderCommentsCreator
import com.intelligentComments.core.domain.core.*
import com.intellij.openapi.components.service
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.RangeMarker
import com.intellij.openapi.project.Project
import com.jetbrains.rd.ide.model.RdContentSegments
import com.jetbrains.rd.ide.model.RdDocComment
import com.jetbrains.rd.ide.model.RdGroupOfLineComments
import com.jetbrains.rd.ide.model.RdIntelligentCommentContent

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
    return content.content.segments.isNotEmpty() && super.isValid()
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

  override fun isValid(): Boolean {
    return super.isValid()
  }

  override fun recreate(editor: Editor): CommentBase {
    return GroupOfLineCommentsFromRd(rdComment, project, rangeMarker)
  }
}