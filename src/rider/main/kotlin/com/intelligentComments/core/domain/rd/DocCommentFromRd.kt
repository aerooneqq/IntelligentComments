package com.intelligentComments.core.domain.rd

import com.intelligentComments.core.comments.RiderCommentsCreator
import com.intelligentComments.core.domain.core.CommentBase
import com.intelligentComments.core.domain.core.DocComment
import com.intelligentComments.core.domain.core.IntelligentCommentContent
import com.intellij.openapi.components.service
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.RangeMarker
import com.intellij.openapi.editor.impl.EditorImpl
import com.intellij.openapi.editor.markup.RangeHighlighter
import com.intellij.openapi.project.Project
import com.jetbrains.rd.ide.model.RdDocComment

class DocCommentFromRd(
  private val rdDocComment: RdDocComment,
  private val project: Project,
  range: RangeMarker
) : CommentFromRd(rdDocComment, project, range), DocComment {
  override val content: IntelligentCommentContent = IntelligentCommentContentFromRd(rdDocComment.content, this, project)

  override fun isValid(): Boolean {
    return content.segments.isNotEmpty() && super.isValid()
  }

  override fun recreate(editor: Editor): CommentBase {
    return project.service<RiderCommentsCreator>().tryCreateDocComment(rdDocComment, editor, rangeMarker)!!
  }
}